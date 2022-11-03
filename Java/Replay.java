// File Name Replay.java
import java.net.*;
import java.io.*;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

class Replay extends JFrame{
	
	JPanel myPanel = new JPanel();
	
	BufferedReader br = null;
	
	Timer timer = null;
	int[] widthAmount={5,8,10,10}; // card row quantity
	int[] heightAmount={4,5,6,8}; // card column quantity
	int level = 0; // game difficulty
	private MyButton[] gameBtn; // game panel game button
	private ImageIcon[] cardImage; // an array represent card
	private boolean gameExist = false;
	
	public Replay(String filePath){ //初始化Replay視窗，並判斷還有沒有record可以replay
		try{
			myPanel.setLayout(new BorderLayout());
			setReader(filePath);
			findGame();
			if (gameExist) setGame();
			else setNull();
			addButton();
		}catch (IOException ioe){
			ioe.printStackTrace();
		}
		setContentPane(myPanel);
		setTitle("Replay");
		setResizable(true);
		setVisible(true);
		pack();
		setSize(600,800);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	private void addButton(){  //加入操作按鈕
		JPanel jp = new JPanel();
		JButton startButton = new JButton("Start");
		startButton.setFocusable(false);
		startButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				start();
			}
		});
		JButton nextButton = new JButton("Next");
		nextButton.setFocusable(false);
		nextButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				next();
			}
		});
		JButton stopButton = new JButton("Stop");
		stopButton.setFocusable(false);
		stopButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				timer.stop();
			}
		});
		JButton closeButton = new JButton("close");
		closeButton.setFocusable(false);
		closeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (timer!=null) timer.stop();
				dispose();
			}
		});
		jp.setLayout(new FlowLayout(FlowLayout.CENTER));
		jp.add(startButton);
		jp.add(stopButton);
		jp.add(nextButton);
		jp.add(closeButton);
		if (!gameExist){
			startButton.setEnabled(false);
			nextButton.setEnabled(false);
			stopButton.setEnabled(false);
			closeButton.setEnabled(false);
		}
		myPanel.add(jp,BorderLayout.SOUTH);
	}

	private void setReader(String fp){ //初始化讀取record的bufferedReader
		File file = new File(fp);
		try{
			FileReader fr = new FileReader(file);
			br = new BufferedReader(fr);
		}catch (FileNotFoundException fnfe){
			try{
				file.createNewFile();
			}catch (IOException ioe){
				ioe.printStackTrace();
			}
		}
	}
	
	private void findGame() throws IOException{ //讀取記錄檔中的紀錄，判斷是否可以replay
		String ins = null;
		if (br!=null){
			ins = br.readLine();
			while (ins!=null && !ins.equals("Start")){
				String[] s = ins.split(" ");
				if(s[0].equals("Level"))
					level = Integer.parseInt(s[1]);
				ins = br.readLine();
			}
		}
		if (ins==null) gameExist = false;
		else if (ins.equals("Start")) gameExist = true;
	}
	
	private void setGame() throws IOException{ //若有紀錄可以replay的話就會執行次function設定版面，由buttons所組成
		JPanel jp = new JPanel();
		jp.setLayout(new GridLayout(heightAmount[level],widthAmount[level]));
		gameBtn = new MyButton[heightAmount[level]*widthAmount[level]];
		cardImage = new ImageIcon[heightAmount[level]*widthAmount[level]];
		String ins;
		String[] s;
		for (int i=0;i<widthAmount[level]*heightAmount[level];i++){
			ins = br.readLine();
			s = ins.split(" ");
			cardImage[i] = new ImageIcon(s[2]);
			gameBtn[i] = new MyButton(i,Color.WHITE);
			gameBtn[i].setImage(cardImage[i]);
			jp.add(gameBtn[i]);
		}
		myPanel.add(jp,BorderLayout.CENTER);
	}
	
	private void setNull(){  //若沒有紀錄可以重播時，就會執行此function
		JLabel jl = new JLabel("No Record Can Replay!");  //顯示此內容在畫面上
		jl.setFont(new Font("MV Boli",Font.ITALIC,20));
		jl.setHorizontalTextPosition(JLabel.CENTER);
		jl.setVerticalTextPosition(JLabel.CENTER);
		jl.setHorizontalAlignment(JLabel.CENTER);
		jl.setVerticalAlignment(JLabel.CENTER);
		myPanel.add(jl,BorderLayout.CENTER);
	}
	
	private void start(){ //開始按鈕執行的function，開始重播
		setTimer();
		timer.start();
	}
	
	private void next(){ //尋找下一輪的遊戲紀錄
		myPanel.removeAll();
		try{
			findGame();
			if (gameExist) setGame();
			else setNull();
			addButton();
		}catch (IOException ioe){
			ioe.printStackTrace();
		}
		myPanel.revalidate();
		myPanel.repaint();
		pack();
		setSize(600,800);
		setLocationRelativeTo(null);
	}
	
	public void setTimer(){ //設定播放的速度
		if (timer!=null) return;
		timer = new Timer(100,new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try{
					String instruction = br.readLine();
					System.out.println(instruction);
					String[] ins = null;
					String f = "";
					while (instruction!=null){
						ins = instruction.split(" ");
						f = ins[0];
						if (f.equals("Flop") || f.equals("Flow") || f.equals("GameOver") || f.equals("Reset")) break;
						instruction = br.readLine();
					}
					if (f.equals("Flop")) flop(Integer.parseInt(ins[1]));
					if (f.equals("Flow")) flow(Integer.parseInt(ins[1]));
					if (f.equals("GameOver") || f.equals("Reset") || ins==null) gameOver();
				}catch (IOException ioe){
					ioe.printStackTrace();
				}
			}
		});
	}
	
	private void flop(int n){  //播放翻牌
		gameBtn[n].showPos();
		gameBtn[n].repaint();
	}
	
	private void flow(int n){  //播放蓋牌
		gameBtn[n].showNeg();
		gameBtn[n].repaint();
	}
	
	private void gameOver(){ //播放結束時執行此function
		timer.stop();
		next();
	}
	
	public static void main(String args[]){
		new Replay("../game_detail.txt");
	}
}