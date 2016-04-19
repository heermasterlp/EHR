package com.um.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.um.data.DiagClassifyData;

public class BasedOnRulePredict {
	
	/**
	 *  Predict medicines based on rules.
	 * @param description
	 * @return
	 */
	public static List<String> predictBasedOnRules(String description){
		if ( description.equals("") ) {
			return null;
		}
		List<String> medicineList = new ArrayList<String>();
		Set<String> medicineSet = new HashSet<String>();
		
		// 1. 石见穿,白花蛇舌草,炒薏仁,龙葵 －－必然出现
		medicineSet.add("石见穿");
		medicineSet.add("白花蛇舌草");
		medicineSet.add("薏苡仁");
		medicineSet.add("龙葵");
		medicineSet.add("茅莓根");
		medicineSet.add("红豆杉");
		
		//3. 气虚－－－太子参 or 党参, 白朮, 黄芪 ＋ 甘草
		if (description.contains("气虚")) {
			medicineSet.add("党参");
			medicineSet.add("白术");
			medicineSet.add("黄芪");
			medicineSet.add("甘草");
		}
		
		// 4. 脾虚
		if (description.contains("脾虚")) {
			medicineSet.add("党参");
			medicineSet.add("白术");
			medicineSet.add("炙黄芪");
			medicineSet.add("甘草");
			medicineSet.add("山药");
		}
		// 5. 阴虚 ---- 北沙参 ＋ 麦冬
		if (description.contains("气阴两虚")) {
			medicineSet.add("北沙参");
			medicineSet.add("麦冬");
			// 气虚
			medicineSet.add("党参");
			medicineSet.add("白术");
			medicineSet.add("黄芪");
			medicineSet.add("甘草");
		}
		
		// 6. 胸肋痛----延胡索，，，牛蒡子OR 木蝴蝶
		if (description.contains("胸肋痛")) {
			medicineSet.add("延胡索");
			Random random = new Random();
			String[] medicines = {"牛蒡子","木蝴蝶"};
			int low = 0;
			int high = 2;
			int result = random.nextInt(high - low) + low;
			medicineSet.add(medicines[result]);
		}
		
		// 7. 厌食 or 食欲减退or纳差--> 炒谷芽 ＋ 山楂
		if (description.contains("厌食")||description.contains("食欲减退") || description.contains("纳差")) {
			medicineSet.add("炒稻芽");
			medicineSet.add("山楂");
		}
		
		// 8. 腹胀 ＋ 便秘 --  轻：厚朴 ＋ 枳壳 ；重： 厚朴 ＋ 生大黄
		if (description.contains("便秘") && (description.contains("重") )) {
			medicineSet.add("番泻叶");
			
		}
		if (description.contains("便秘") && (description.contains("轻") || description.contains("中"))) {
			medicineSet.add("枳实");
			medicineSet.add("火麻仁");
		}
		// 9. 睡眠差 －－  酸枣仁 ＋ 磁石
		if (description.contains("失眠") && (description.contains("中") || description.contains("重"))) {
			medicineSet.add("酸枣仁");
			medicineSet.add("磁石");
		}
		// 10. 红血痰 －－－ 紫珠草 ＋ 三七
		if (description.contains("红血痰")) {
			medicineSet.add("紫珠草");
			medicineSet.add("三七末");
		}
		// 11. 口渴多饮 －－－ 石斛 ＋ 天仁粉
		if (description.contains("口渴多饮")) {
			medicineSet.add("石斛");
			medicineSet.add("天仁粉");
		}
		
		// 12. 腹泻 泄泻 ----- 石榴皮，五味子，补骨脂， 淮山药
		if (description.contains("腹泻")) {
			medicineSet.add("石榴皮");
			medicineSet.add("五味子");
			medicineSet.add("补骨脂");
			medicineSet.add("山药");
			medicineSet.add("葶苈子");
		}
		
		// 13. 头晕
		if (description.contains("头晕")) {
			medicineSet.add("天麻");
			medicineSet.add("磁石");
		}
		// 14. 乏力
		if (description.contains("气力差") || description.contains("气力特差")) {
			medicineSet.add("黄芪");
			medicineSet.add("黄精");
			medicineSet.add("桑葚");
			medicineSet.add("肉桂蓉");
		}
		
		// 15. 咳嗽
		if (description.contains("咳嗽轻") || description.contains("咳嗽中") || description.contains("咳嗽重")) {
			medicineSet.add("白茅根");
		}
		
		medicineList.addAll(medicineSet);
		// fix dangshen and taizishen
		medicineList = EhealthUtil.fixMedicineList(medicineList);
		// sorted
		List<String> medicineListByStatisticSorted = new ArrayList<String>();
		for( String s : DiagClassifyData.machineMedicine ){
			if (medicineList.contains(s)) {
				medicineListByStatisticSorted.add(s);
			}
		}
		
		return medicineListByStatisticSorted;
	}
}