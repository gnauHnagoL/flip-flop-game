import javax.swing.*;
import java.awt.*;
import java.awt.color.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.*;
import java.util.*;
import java.io.*;

public class Settlement { 

	JPanel panel = new JPanel();
	JLabel rank1,rank2,rank3,rank4;
	JLabel player1,player2,player3,player4;
	JLabel score1,score2,score3,score4;
	Game game;
	
	public Settlement(Game game, String[] name, int[] s){
		this.game = game;
		sort(name,s);
		update(name,s);
		out();
	}
	
	private void sort(String[] name, int[] s){ //排序分數
		String tmps;
		int tmpi;
		for (int i=0;i<s.length;i++){
			int mxInd = i;
			for (int j=i+1;j<s.length;j++)
				if (s[j]>s[mxInd]) mxInd = j;
			tmps = name[i];
			name[i] = name[mxInd];
			name[mxInd] = tmps;
			tmpi = s[i];
			s[i] = s[mxInd];
			s[mxInd] = tmpi;
		}
	}
	
	private void update(String[] name, int[] s){ //把排序好的分數依序排名
		rank1 = new JLabel("No.1");
		rank2 = new JLabel("No.2");
		rank3 = new JLabel("No.3");
		rank4 = new JLabel("No.4");
		player1 = new JLabel(name[0]);
		player2 = new JLabel(name[1]);
		player3 = new JLabel(name[2]);
		player4 = new JLabel(name[3]);
		score1 = new JLabel(String.valueOf(s[0]));
		score2 = new JLabel(String.valueOf(s[1]));
		score3 = new JLabel(String.valueOf(s[2]));
		score4 = new JLabel(String.valueOf(s[3]));
	}
	
	private void out(){ //顯示結算畫面
		JOptionPane.showMessageDialog(game,getPanel(),"result",JOptionPane.PLAIN_MESSAGE);
	}
	
	private JPanel getPanel(){ //設置結算畫面
		panel.removeAll();
		panel.setLayout(new GridLayout(4,3));
		panel.add(rank1);
		panel.add(player1);
		panel.add(score1);
		panel.add(rank2);
		panel.add(player2);
		panel.add(score2);
		panel.add(rank3);
		panel.add(player3);
		panel.add(score3);
		panel.add(rank4);
		panel.add(player4);
		panel.add(score4);
		panel.revalidate();
		panel.repaint();
		return panel;
	}
}