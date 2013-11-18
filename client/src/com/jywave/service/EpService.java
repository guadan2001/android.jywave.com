package com.jywave.service;

import java.util.Date;

import com.jywave.vo.Ep;

public class EpService {
	
	public EpService()
	{
		
	}
	
	public Ep getEp(int id)
	{
		Ep ep = new Ep();
		
		switch(id)
		{
		case 1:
			ep.id = 1;
			ep.title = "酱油冲击播-VOL.037-13-11-11";
			ep.star = 5;
			ep.length = 2539;
			ep.status = Ep.IN_SERVER;
			ep.isNew = true;
			ep.description = "酱油冲击播第37期开播，又到了人民群众又爱又恨的光棍节了，和往年一样，酱油微播准备了一期音乐专题陪你过节。\n\n主播：GD\n\n歌曲列表：\n1. 李志 – 天空之城\n2. 杨宗纬 – 对爱渴望\n3. Maximilian Hecker – Fool\n4. Coldplay – Fix You\n5. Jonathan Steingard – Heart Set\n6. 张震岳 – 在凌晨\n7. Lady Antebellum – Need You Now\n8. Green Day – Last Night On Earth\n9. 刘若英 – 幸福不是情歌";
			ep.url = "http://www.jywave.com/blog/podpress_trac/web/626/0/jyshock037.mp3";
			ep.coverUrl = "http://android.jywave.com/images/jyshock037-128.jpg";
			ep.publishDate = new Date();
			break;
		case 2:
			ep.id = 2;
			ep.title = "酱油冲击播-VOL.036-清晨醒来RELOAD";
			ep.star = 4;
			ep.length = 2419;
			ep.status = Ep.IN_LOCAL;
			ep.isNew = true;
			ep.description = "酱油冲击播第36期开播，在酱油微播开播两周年之际，这期炒冷饭的节目希望能够带给你动力，帮你在清晨满血复活，感谢每个支持酱油微播的听众！\n\n主播：GD\n\n曲目列表：\n1. Mandy Moore – When Will My Life Begin\n2. 王若琳 – Let’s Start From Here\n3. The Primitives – Crash\n4. Karmin – Brokenhearted\n5. Keane – Bend & Break\n6. Armin van Buuren – 4 Elements\n7. Owl City & Carly Rae Jepsen – Good Time\n8. Owl City – Strawberry Avalanche\n9. Hans Zimmer & John Powell – Dumpling Warrior Remix";
			ep.url = "http://www.jywave.com/blog/podpress_trac/web/618/0/jyshock036.mp3";
			ep.coverUrl = "http://android.jywave.com/images/jyshock036-128.jpg";
			ep.publishDate = new Date();
			break;
		case 3:
			ep.id = 3;
			ep.title = "酱油播动拳-VOL.028-城市-西安";
			ep.star = 3;
			ep.length = 5722;
			ep.status = Ep.DOWNLOADING;
			ep.isNew = false;
			ep.description = "酱油播动拳第28期开播，西安是我们每天生活的城市，本期是城市系列的第一期，跟大家聊聊西安，关键字：美食、小偷、交大……\n\n主播：晓星、段超、GD";
			ep.url = "http://www.jywave.com/blog/podpress_trac/web/610/0/jyhadouken028.mp3";
			ep.coverUrl = "http://android.jywave.com/images/jyhadouken028-128.jpg";
			ep.publishDate = new Date();
			break;
		case 4:
			ep.id = 4;
			ep.title = "酱油冲击播-VOL.035-RONALD JENKEES";
			ep.star = 2;
			ep.length = 2360;
			ep.status = Ep.PLAYING;
			ep.isNew = false;
			ep.description = "酱油冲击播第35期开播，Ronald Jenkees是在YOUTUBE上走红的即兴电子混音奇才，本期节目将和你一起聆听他那些精彩的电音作品。\n\n主播：GD\n\n歌曲列表：\n1. Disorganized Fun\n2. Outer Space\n3. Fifteen Fifty\n4. Speaker 1, Speaker 1\n5. Piano Wire\n6. Super-Fun\n7. Canon in D Remix\n8. Guitar Sound\n9. Stay Crunchy";
			ep.url = "http://www.jywave.com/blog/podpress_trac/web/604/0/jyshock035.mp3";
			ep.coverUrl = "http://android.jywave.com/images/jyshock035-128.jpg";
			ep.publishDate = new Date();
			break;
		}
		
		return ep;
		
	}

}
