import javax.swing.*;
import java.awt.*;

public class MyButton extends JButton{
	
	private int number = -1;
	private ImageIcon neg = new ImageIcon("background1.jpg");
	private ImageIcon pos = null;
	private ImageIcon current;
	
	public MyButton(int number, Color color){
		this.number = number;
		this.current = neg;
		// this.neg = pos; // for testing
		this.setBackground(color);
		this.setBorder(BorderFactory.createRaisedBevelBorder());
		this.setFocusable(false);
		this.setEnabled(false);
	}
	
	public int getNumber(){
		return number;
	}
	public void showPos(){ //把牌的圖片設為正面
		current = pos;
	}
	public void showNeg(){ //把牌的圖片設為背面
		current = neg;
	}
	public String getImageURL(){
		return pos.toString();
	}
	public void setImage(ImageIcon pos){
		this.pos = pos;
	}
	public void paintComponent(Graphics g){ //調整卡牌圖片為按鈕的大小
		super.paintComponent(g);
		g.drawImage(current.getImage(),0,0,this.getWidth(),this.getHeight(),this);
	}
}
