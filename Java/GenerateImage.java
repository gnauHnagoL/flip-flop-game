import javax.swing.*;
import java.util.*;

public class GenerateImage{
	public static ImageIcon[] generate(int level, int quantity){
		//設置圖片陣列，並放入相對應號碼的圖片
		ImageIcon[] img = new ImageIcon[quantity];
		for (int i=0;i<quantity/2;i++){
			img[i] = new ImageIcon("level"+String.valueOf(level)+"/"+String.format("%02d",i)+".jpg");
			img[i+quantity/2] = new ImageIcon("level"+String.valueOf(level)+"/"+String.format("%02d",i)+".jpg");
		}
		List<ImageIcon> imgList = Arrays.asList(img);
		Collections.shuffle(imgList); //隨機排列這些圖片的順序
		img = imgList.toArray(new ImageIcon[0]);
		return img;
	}
}	