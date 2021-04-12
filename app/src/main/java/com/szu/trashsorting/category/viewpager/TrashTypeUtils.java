package com.szu.trashsorting.category.viewpager;

import com.szu.trashsorting.R;

public class TrashTypeUtils {

    public static int[] picture = {R.mipmap.recyclables,R.mipmap.hazardouswaste,R.mipmap.wetgarbage,R.mipmap.dryrefuse};

    public static String[] title = {"可回收垃圾","有害垃圾","湿垃圾","干垃圾"};

    public static String[] explains = {
            "可回收垃圾是指适宜回收、可循环利用的生活废弃物",
            "有毒有害垃圾是指存有对人体健康有害的重金属、有毒的物质或者对环境造成现实危害或者潜在危害的废弃物",
            "厨余垃圾（湿垃圾）是指居民日常生活及食品加工、饮食服务、单位供餐等活动中产生的垃圾",
            "干垃圾即其它垃圾，指除可回收物、有害垃圾、厨余垃圾（湿垃圾）以外的其它生活废弃物"
    };

    public static String[] contains = {
            "常见包括各类废金属、玻璃瓶、易拉罐、饮料瓶、塑料玩具、书本、报纸、广告单、纸板箱、衣服、床上用品、电子产品等",
            "常见包括废电池、废荧光灯管、废灯泡、废水银温度计、废油漆桶、过期药品、农药、杀虫剂等",
            "常见包括菜叶、剩菜、剩饭、果皮、蛋壳、茶渣、骨头等",
            "常见包括砖瓦陶瓷、渣土、卫生间废纸、猫砂、污损塑料、毛发、硬壳、一次性制品、灰土、瓷器碎片等难以回收的废弃物"
    };

    public static String[] tips = {
            "·轻投轻放\n\n清洁干燥，避免污染，费纸尽量平整\n\n·立体包装物请清空内容物，清洁后压扁投放\n\n·有尖锐边角的、应包裹后投放\n",
            "·分类投放有害垃圾时，应注意轻放\n\n·废灯管等易破损的有害垃圾应连带包装或包裹后投放\n\n·废弃药品宜连带包装一并投放\n\n·杀虫剂等压力罐装容器，应排空内容物后投放\n\n·在公共场所产生有害垃圾且未发现对应收集容器时，应携带至有害垃圾投放点妥善投放",
            "·纯流质的食物垃圾、如牛奶等，应直接倒进下水口\n\n·有包装物的湿垃圾应将包装物去除后分类投放,包装物请投放到对应的可回收物或干垃圾容器",
            "·尽量沥干水分\n\n·难以辨识类别的生活垃圾都可以投入干垃圾容器内"
    };


    public static String[] url = {
            "https://baike.baidu.com/item/%E5%8F%AF%E5%9B%9E%E6%94%B6%E7%89%A9/2479461?fromtitle=%E5%8F%AF%E5%9B%9E%E6%94%B6%E5%9E%83%E5%9C%BE&fromid=2084517&fr=aladdin",
            "https://baike.baidu.com/item/%E6%9C%89%E5%AE%B3%E5%9E%83%E5%9C%BE/5677220?fr=aladdin",
            "https://baike.baidu.com/item/%E6%B9%BF%E5%9E%83%E5%9C%BE/23589703?fr=aladdin",
            "https://baike.baidu.com/item/%E5%B9%B2%E5%9E%83%E5%9C%BE/23589706?fr=aladdin"
    };

    public TrashTypeEntity getTrash(int index){

        TrashTypeEntity trashTypeEntity = new TrashTypeEntity();

        trashTypeEntity.setTitle(title[index]);
        trashTypeEntity.setPicture(picture[index]);
        trashTypeEntity.setUrl(url[index]);
        trashTypeEntity.setContain(contains[index]);
        trashTypeEntity.setExplain(explains[index]);
        trashTypeEntity.setTip(tips[index]);

        return trashTypeEntity;
    }

}
