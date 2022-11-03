// File Name Server.java
import java.net.*;
import java.io.*;
import java.util.*;
import java.text.*;

public class Server{
	
	protected static List<Socket> sockets = new Vector<>(); //將接收到的socket變成一個集合
	protected static int player = -1;
	protected static boolean[] readyState = {true,false,false,false};
	protected static String[] playerName = {"player1","player2","player3","player4"};
	protected static int[] playerScore = {0,0,0,0};
	protected static int playerAmount = 0;
	protected static int round = -1;
	protected static int rounds = 1;
	protected static int level = 0;
	protected static int[] cardAmount = {10,20,30,40};
	protected static int countAdd = 0;
	
	public static void main(String[] args) throws IOException{
		initialLog(); //執行初始化紀錄檔function
		ServerSocket serverSocket = new ServerSocket(6666); //建立服務端
		while(playerAmount<=4) {
			try{
				System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
				Socket accept = serverSocket.accept(); //阻塞等待客戶端連線
				player++;
				playerAmount++;
				synchronized (sockets){
					sockets.add(accept); //接受客戶端請求
				}
				Thread thread = new Thread(new ServerThread(accept)); //多個伺服器執行緒進行對客戶端的響應
				thread.start(); //啟用執行緒
			}
			catch (IOException e) { //異常時顯示錯誤訊息
				e.printStackTrace();
				break;
			}
		}
		serverSocket.close(); //關閉伺服器
	}
	
	public static void initialLog(){ //初始化紀錄檔
        try{
			File file =new File("../game_detail.txt"); 
			if (file.exists()) file.delete(); //把舊的紀錄檔刪除
			if(!file.exists()) file.createNewFile(); //創立新的紀錄檔
        }
        catch(FileNotFoundException e) {
            System.exit(0);
        }
		catch(IOException e){
			System.exit(0);
		}
	}
}

class ServerThread extends Server implements Runnable {

	private Socket socket; //宣告名為socket的socket
	private String socketName;
	
	private BufferedReader reader; //宣告名為reader的BufferedReader

	public ServerThread(Socket socket){ //伺服器執行緒，主要處理多個客戶端請求
		this.socket = socket; //初始化socket內容
		System.out.println(playerName[player]+" joined the room."); //顯示哪一個客戶端已經加入
		socketName = playerName[player];
	}

	public void run(){
		try{
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while (true){
				String line = reader.readLine(); //設定line為接收到的內容
				System.out.println("From "+socketName+": "+line); //debug
				String[] instruction = line.split(" ");
				if (line == null) break; //當達成條件時則跳出迴圈
				writetxt(line); //把收到的內容寫入紀錄檔
				//收到不同的字串，會去做相對應的處理
				switch (instruction[0]){
				case "GetInformation": // deliver another player information (name and state)
					transmitInfo();
					break;
				case "Name": // set user name
					playerName[player] = instruction[1]; 
					socketName = instruction[1];
					break;
				case "CheckStart":
					checkStart();
					break;
				case "Start":
					print(line);
					nextPlayer();
					break;
				case "Level":
					print(line);
					level = Integer.parseInt(instruction[1]);
					break;
				case "Ready":
					setReady(Integer.parseInt(instruction[1]),Boolean.valueOf(instruction[2]));
					break;
				case "RoundEnd":
					nextPlayer();
					break;
				case "Reset":
					print(line);
					reset();
					break;
				case "Add":
					print(line);
					addPoint(line);
					detectEnd();
					break;
				default: //使用print function輸出內容
					print(line);
				}
			}
			closeConnect(); //斷開連線
		}
		catch (IOException e) { //異常時斷開連線
			closeConnect();
		}
	}
	
	public void print(String msg) throws IOException{
		PrintWriter out = null; //宣告out
		synchronized (sockets){ //把sockets鎖住，讓他專注執行下面的程序
			for (Socket sc : sockets){ //一個一個廣播出去
				if (socket == sc) continue; 
				out = new PrintWriter(sc.getOutputStream(),true); //設定out為socket輸出的內容
				out.println(msg); //把訊息放到out中
			}
		}
	}
	
	public void transmitInfo() throws IOException{ //傳送玩家的詳細資訊
		PrintWriter out = null;
		out = new PrintWriter(socket.getOutputStream(),true);
		out.println("Number "+player); //告知客戶端他的玩家號碼
		synchronized (sockets){
			for (Socket sc: sockets){
				out = new PrintWriter(sc.getOutputStream(),true);
				for (int i=0;i<4;i++){
					out.println("Name "+i+" "+playerName[i]); //顯示玩家名稱
					out.println("State "+i+" "+readyState[i]); //顯示玩家狀態
				}
			}
		}
	}
	
	public void checkStart() throws IOException{ //檢查玩家是否都已經準備
		PrintWriter out = null;
		out = new PrintWriter(socket.getOutputStream(),true);
		for (int i=0;i<playerAmount;i++) if (!readyState[i]) return;
		out.println("CanStart"); //當確定玩家都已經準備，則傳送可以開始的訊息給房主
	}
	
	public void nextPlayer() throws IOException{ //設置輪流翻牌
		round++; 
		if (round>playerAmount-1){ //round大於玩家數量時就回到第一個玩家開始翻牌
			rounds++;
			round=0;
		}
		PrintWriter out = null;
		synchronized (sockets){
			for (Socket sc: sockets){
				out = new PrintWriter(sc.getOutputStream(),true);
				out.println("RightOfFlop "+round+" "+rounds); //告知客戶端目前是幾號玩家可以翻牌，並顯示這是第幾回合
			}
		}
	}
	
	public void setReady(int i, boolean b) throws IOException{
		readyState[i] = b; //設置玩家準備狀態
		print("Ready "+i+" "+b); //顯示哪個玩家已經準備
	}
	
	public void addPoint(String line){  //加上得到的分數
		String[] spl = line.split(" ");
		playerScore[Integer.parseInt(spl[1])]+=Integer.parseInt(spl[2]);
	}
	
	public void detectEnd() throws IOException{ //偵測遊戲是否結束
		countAdd++;
		if (countAdd == cardAmount[level]){  //當卡牌全部被翻完就代表遊戲結束
			PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
			out.println("GameOver");  //顯示gameover
			print("GameOver");
			writetxt("GameOver");
			writeResult();
			reset(); //遊戲結束後自動把遊戲重置
		}
	}
	
	public void reset(){ //遊戲重置
		for (int i=0;i<4;i++) readyState[i] = (i==0)?true:false; //把所有玩家的準備狀態設為尚未準備
		for (int i=0;i<4;i++) playerScore[i] = 0;
		round = -1; //把可翻牌的玩家號碼設成-1，代表還沒有玩家能夠翻牌
		rounds = 1; //把回合數重置
		countAdd=0; //已成功翻開的卡片數量重置
	}
	
	public void closeConnect(){ //斷開連線的function
		try{
			System.out.println(socketName+" left the room."); //顯示哪一個客戶端離開
			synchronized (sockets){
				sockets.remove(socket); //移除server端與client所建立的socket
			}
			socket.close(); //關閉socket
			player--;
		}
		catch (IOException e){ //異常時顯示錯誤訊息
			System.out.println("Close not sucess.");
		}
	}

	public void writetxt(String line){ //撰寫紀錄檔
		try{
			File file =new File("../game_detail.txt");
			FileWriter fileWritter = new FileWriter(file,true);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			bufferWritter.write(line); //把收到的訊息寫在txt上
			bufferWritter.newLine();
			bufferWritter.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void writeResult(){  //撰寫遊戲結果檔案
		String[] tmpName = new String[4];
		for (int i=0;i<4;i++) tmpName[i] = playerName[i];
		String tmps;
		int tmpi;
		for (int i=0;i<tmpName.length;i++){
			int mxInd = i;
			for (int j=i+1;j<tmpName.length;j++)
				if (playerScore[j]>playerScore[mxInd]) mxInd = j;
			tmps = playerName[i];
			playerName[i] = playerName[mxInd];
			playerName[mxInd] = tmps;
			tmpi = playerScore[i];
			playerScore[i] = playerScore[mxInd];
			playerScore[mxInd] = tmpi;
		}
		try{
			File file = new File("../game_result.txt");
			BufferedWriter bw = new BufferedWriter(new FileWriter(file,true));
			bw.write(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
			bw.newLine();
			for (int i=0;i<4;i++){
				bw.write("No."+(i+1)+" "+tmpName[i]+" "+playerScore[i]);
				bw.newLine();
			}
			bw.newLine();
			bw.close();
		}catch (IOException e){
			e.printStackTrace();
		}
	}
}