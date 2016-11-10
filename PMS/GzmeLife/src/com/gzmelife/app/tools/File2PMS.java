package com.gzmelife.app.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class File2PMS {
public PmsFile getfile(String PathName){
	PmsFile retFile=null;
	InputStream in;
	File file;
	if (PathName != null) {
		file = new File(PathName);
	}else{
		return retFile;
	}
	try {
		in = new FileInputStream(file);
		byte b[] = new byte[(int) file.length()]; // 创建合适文件大小的数组
		in.read(b); // 读取文件中的内容到b[]数组
		in.close();
		// EncodingUtils.getString("", "");
		retFile = new PmsFile(b);
//		if (newLog == null) {
//			iv_photo.setBackgroundResource(R.drawable.icon_pms_file);
//			ivState = true;
//			backgroundResource = R.drawable.icon_pms_file;
//		} else {
//			iv_photo.setImageBitmap(newLog);
//			bitmap = pmsFile.bitmap;
//			ivState = false;
//		}

//		et_describe.setText(pmsFile.text);
//		StringBuffer material = new StringBuffer();
//		listTimeNode.clear();
//		MyLog.d("pmsFile.listTimeNode != null : " + (listTimeNode != null));
//		TimeNode node = new TimeNode();
//
//		if (pmsFile.listTimeNode != null) {
//			if (pmsFile.listTimeNode.size() == 0) {
//				pmsFile.listTimeNode.add(node);
//			}
//			for (int i = 0; i < pmsFile.listTimeNode.size(); i++) {
//				for (int j = 0; j < pmsFile.listTimeNode.get(i).FoodNames.length; j++) {
//					if (!TextUtils
//							.isEmpty(pmsFile.listTimeNode.get(i).FoodNames[j])) {
//						material.append(pmsFile.listTimeNode.get(i).FoodNames[j]);
//						if (i != pmsFile.listTimeNode.size() - 1
//								&& j != pmsFile.listTimeNode.get(i).FoodNames.length - 1) {
//							material.append(";");
//						}
//					}
//				}
//			}
//			listTimeNode.addAll(pmsFile.listTimeNode);
//			listTimeNode.set(Config.position, timeNode);
//		}
//		// String[] str=material.toString().split(";");
//		tv_material2.setText(material.toString());
//		cookBookStopAdapter.notifyDataSetChanged();
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
	return retFile;
}
}
