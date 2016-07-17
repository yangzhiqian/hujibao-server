package edu.ncu.yang.utils;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class BitmapUtil {

	public static void toSmallImage(File oldFile, File newFile, int width, int height,float quality)
			throws IOException {
		if (!newFile.getParentFile().exists()) {
			newFile.getParentFile().mkdirs();
		}
		Image src = null;
		BufferedImage tag = null;

		src = javax.imageio.ImageIO.read(oldFile); // 构造Image对象
		int old_w = src.getWidth(null) == -1 ? width : src.getWidth(null); // 得到源图宽
		int old_h = src.getHeight(null) == -1 ? height : src.getHeight(null);
		int new_w = 0;
		int new_h = 0; // 得到源图长
		double w2 = (old_w * 1.00) / (width * 1.00);
		double h2 = (old_h * 1.00) / (height * 1.00);
		// 图片调整为方形结束
		if (old_w > width){
			new_w = (int) Math.round(old_w / w2);
		}else{
			new_w = old_w;
		}
		if (old_h > height){
			new_h = (int) Math.round(old_h / h2);// 计算新图长宽
		}else{
			new_h = old_h;
		}
		tag = new BufferedImage(new_w, new_h, BufferedImage.TYPE_INT_RGB);
		tag.getGraphics().drawImage(
				src.getScaledInstance(new_w, new_h, Image.SCALE_SMOOTH), 0, 0,
				null);

		ImageIO.write(tag,  "jpeg" , newFile); 
		tag.flush();
	}

}