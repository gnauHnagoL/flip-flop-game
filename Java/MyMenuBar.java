import javax.sound.sampled.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.io.*;

public class MyMenuBar {
	public static Clip clip = null;
	public File music = new File("bgm0.wav");
	public Game f;

    public MyMenuBar() {
		try { //遊戲一開始會先預設BGM
			StartMusic(music);
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
			e1.printStackTrace();
		}	
	}


    public void setmenu(Game f) { //設置menubar
		this.f = f;
        JMenuBar MBar = new JMenuBar();
        JMenu thefile = new JMenu("Set");
        
        JMenu BGM = new JMenu("BGM");
		JMenuItem choose = new JMenuItem("Choose BGM");
		JMenuItem stop = new JMenuItem("Stop BGM");
		JMenuItem play = new JMenuItem("Play BGM");
			choose.addActionListener( new ActionListener() { //選擇BGM
				public void actionPerformed(ActionEvent e){
					JFileChooser fileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
					fileChooser.setFileFilter(new FileNameExtensionFilter(null,"wav"));
					int result = fileChooser.showOpenDialog(null);
					if(result == JFileChooser.APPROVE_OPTION){
						try {
							StartMusic(fileChooser.getSelectedFile());
							music = fileChooser.getSelectedFile();
						} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}           
			});
			
			stop.addActionListener( new ActionListener() { //暫停BGM
                public void actionPerformed(ActionEvent e){
					    try {
							StopMusic();
						} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
				}
			});
					
			play.addActionListener( new ActionListener() { //繼續撥放BGM
                public void actionPerformed(ActionEvent e){	
					    try {
							PlayMusic();
						} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
				}
			});
		JMenu color = new JMenu("Color");
        JMenuItem Pcolor = new JMenuItem("Primary Panel Background");  
		JMenuItem Scolor = new JMenuItem("Secondary Panel Background");
		ActionListener colorAction = new ActionListener(){ //設置背景顏色
			public void actionPerformed(ActionEvent e){
				Color initialBackground = new Color(255, 255, 0);
				Color background = JColorChooser.showDialog(null, "JColorChooser Sample", initialBackground);
				if (e.getSource()==Pcolor) setPrimaryColor(background);
				if (e.getSource()==Scolor) setSecondaryColor(background);
			}
		};
		Pcolor.addActionListener(colorAction);
		Scolor.addActionListener(colorAction);
        
		BGM.add(choose);
		BGM.add(stop);
		BGM.add(play);
		color.add(Pcolor);
		color.add(Scolor);
        thefile.add(BGM);
        thefile.add(color);

        MBar.add(thefile); 

        f.setJMenuBar(MBar);
    }

    public static void StartMusic(File file) 
	throws UnsupportedAudioFileException, IOException, LineUnavailableException{
	    AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
	    if (clip != null) { //若已經選擇BGM，則把BGM暫停
	    	clip.stop();
	    }
		//播放新選擇的BGM
  	    clip = AudioSystem.getClip();  
  	    clip.open(audioStream);
	    clip.loop(Clip.LOOP_CONTINUOUSLY);
	    clip.start();
		clip.stop(); // for coding to reduce noise
	}
	
	public static void StopMusic()
	throws UnsupportedAudioFileException, IOException, LineUnavailableException{
		clip.stop();
    }
	
	public static void PlayMusic()
	throws UnsupportedAudioFileException, IOException, LineUnavailableException{
	    clip.loop(Clip.LOOP_CONTINUOUSLY);
	    clip.start();
    }
	private void setPrimaryColor(Color color){ //設置最底層背景顏色
		if (color != null) {
			f.getContentPane().setBackground(color);
		}
	}
	private void setSecondaryColor(Color color){  //統一設置各區塊版面顏色
		if (color != null) {
			if (f.gaming_zone != null) f.gaming_zone.setBackground(color);
			if (f.control != null) f.control.setBackground(color);
			if (f.scoreboard != null) f.scoreboard.setBackground(color);
			if (f.player_IP != null) f.player_IP.setBackground(color);
			if (f.text_area != null) f.text_area.setBackground(color);
		}
	}
}