package com.jywave.provider;

import java.util.Date;

import com.jywave.AppMain;
import com.jywave.vo.Ep;

public class EpProvider {
	
	public EpProvider()
	{
		
	}
	
	public Ep getEp(int id)
	{
		Ep ep = new Ep();
		
		switch(id)
		{
		case 0:
			ep.id = 0;
			ep.title = "酱油播动拳-VOL.029-跑步(上)";
			ep.star = 5;
			ep.duration = 2285;
			ep.status = Ep.IN_SERVER;
			ep.isNew = true;
			ep.description = "酱油播动拳第29期开播，在本期节目中，我们请到来自哈尔滨的跑步达人远志，跟大家分享有关跑步的话题，很涨姿势，很涨姿势。\n\n主播：GD、晓星、taubau、大刀、open\n\n嘉宾：远志";
			ep.url = AppMain.debugServer + "podcasts/jyhadouken029.mp3";
			ep.coverUrl = AppMain.debugServer + "images/jyhadouken029.jpg";
			ep.coverThumbnailUrl = AppMain.debugServer + "images/jyhadouken029_200x200.jpg";
			ep.publishDate = new Date();
			break;
		case 1:
			ep.id = 1;
			ep.title = "酱油冲击播-VOL.037-13-11-11";
			ep.star = 5;
			ep.duration = 2539;
			ep.status = Ep.IN_SERVER;
			ep.isNew = true;
			ep.description = "酱油冲击播第37期开播，又到了人民群众又爱又恨的光棍节了，和往年一样，酱油微播准备了一期音乐专题陪你过节。\n\n主播：GD\n\n歌曲列表：\n1. 李志 – 天空之城\n2. 杨宗纬 – 对爱渴望\n3. Maximilian Hecker – Fool\n4. Coldplay – Fix You\n5. Jonathan Steingard – Heart Set\n6. 张震岳 – 在凌晨\n7. Lady Antebellum – Need You Now\n8. Green Day – Last Night On Earth\n9. 刘若英 – 幸福不是情歌";
			ep.url = AppMain.debugServer + "podcasts/jyshock037.mp3";
			ep.coverUrl = AppMain.debugServer + "images/jyshock037.jpg";
			ep.coverThumbnailUrl = AppMain.debugServer + "images/jyshock037_200x200.jpg";
			ep.publishDate = new Date();
			break;
		case 2:
			ep.id = 2;
			ep.title = "酱油冲击播-VOL.036-清晨醒来RELOAD";
			ep.star = 4;
			ep.duration = 2419;
			ep.status = Ep.IN_SERVER;
			ep.isNew = true;
			ep.description = "酱油冲击播第36期开播，在酱油微播开播两周年之际，这期炒冷饭的节目希望能够带给你动力，帮你在清晨满血复活，感谢每个支持酱油微播的听众！\n\n主播：GD\n\n曲目列表：\n1. Mandy Moore – When Will My Life Begin\n2. 王若琳 – Let’s Start From Here\n3. The Primitives – Crash\n4. Karmin – Brokenhearted\n5. Keane – Bend & Break\n6. Armin van Buuren – 4 Elements\n7. Owl City & Carly Rae Jepsen – Good Time\n8. Owl City – Strawberry Avalanche\n9. Hans Zimmer & John Powell – Dumpling Warrior Remix";
			ep.url = AppMain.debugServer + "podcasts/jyshock036.mp3";
			ep.coverUrl = AppMain.debugServer + "images/jyshock036.jpg";
			ep.coverThumbnailUrl = AppMain.debugServer + "images/jyshock036_200x200.jpg";
			ep.publishDate = new Date();
			break;
		case 3:
			ep.id = 3;
			ep.title = "酱油播动拳-VOL.028-城市-西安";
			ep.star = 3;
			ep.duration = 5722;
			ep.status = Ep.IN_SERVER;
			ep.isNew = false;
			ep.description = "酱油播动拳第28期开播，西安是我们每天生活的城市，本期是城市系列的第一期，跟大家聊聊西安，关键字：美食、小偷、交大……\n\n主播：晓星、段超、GD";
			ep.url = AppMain.debugServer + "podcasts/jyhadouken028.mp3";
			ep.coverUrl = AppMain.debugServer + "images/jyhadouken028.jpg";
			ep.coverThumbnailUrl = AppMain.debugServer + "images/jyhadouken028_200x200.jpg";
			ep.publishDate = new Date();
			break;
		case 4:
			ep.id = 4;
			ep.title = "酱油冲击播-VOL.035-RONALD JENKEES";
			ep.star = 2;
			ep.duration = 2360;
			ep.status = Ep.IN_SERVER;
			ep.isNew = false;
			ep.description = "酱油冲击播第35期开播，Ronald Jenkees是在YOUTUBE上走红的即兴电子混音奇才，本期节目将和你一起聆听他那些精彩的电音作品。\n\n主播：GD\n\n歌曲列表：\n1. Disorganized Fun\n2. Outer Space\n3. Fifteen Fifty\n4. Speaker 1, Speaker 1\n5. Piano Wire\n6. Super-Fun\n7. Canon in D Remix\n8. Guitar Sound\n9. Stay Crunchy";
			ep.url = AppMain.debugServer + "podcasts/jyshock035.mp3";
			ep.coverUrl = AppMain.debugServer + "images/jyshock035.jpg";
			ep.coverThumbnailUrl = AppMain.debugServer + "images/jyshock035_200x200.jpg";
			ep.publishDate = new Date();
			break;
		case 5:
			ep.id = 5;
			ep.title = "酱油播动拳-VOL.027-论五毛";
			ep.star = 1;
			ep.duration = 6055;
			ep.status = Ep.IN_SERVER;
			ep.isNew = false;
			ep.description = "酱油播动拳第27期开播，高法的司法解释推出后引起了大家的热议，本期节目与大家分享一些有关网络话语环境方面的问题。\n\n主播：晓星、GD\n\n第一次现场录音，对音量的控制不是特别精确，个别部分效果欠佳，请大家谅解。";
			ep.url = AppMain.debugServer + "podcasts/jyhadouken027.mp3";
			ep.coverUrl = AppMain.debugServer + "images/jyhadouken027.jpg";
			ep.coverThumbnailUrl = AppMain.debugServer + "images/jyhadouken027_200x200.jpg";
			ep.publishDate = new Date();
			break;
		case 6:
			ep.id = 6;
			ep.title = "酱油冲击播-VOL.034-大合唱";
			ep.star = 1;
			ep.duration = 2978;
			ep.status = Ep.IN_SERVER;
			ep.isNew = false;
			ep.description = "酱油冲击播第34期开播，本期节目将与您一起回顾和分享一些著名演唱会中的大合唱，感受现场演唱的魅力。\n\n主播：GD\n\n歌曲列表：\n1. Slash – Sweet Child o’Mine\n2. Oasis – Don’t Look Back In Anger\n3. Coldplay – Viva la Vida\n4. Bryan Adams – (Everything I Do) I Do It for You\n5. 信乐团 – 假如\n6. 周杰伦 – 黑色幽默\n7. X-Japan – Tears\n8. Adele – Someone Like You\n9. Beyond – 海阔天空";
			ep.url = AppMain.debugServer + "podcasts/jyshock034.mp3";
			ep.coverUrl = AppMain.debugServer + "images/jyshock034.jpg";
			ep.coverThumbnailUrl = AppMain.debugServer + "images/jyshock034_200x200.jpg";
			ep.publishDate = new Date();
			break;
		case 7:
			ep.id = 7;
			ep.title = "酱油冲击播-VOL.033-川井宪次-进击";
			ep.star = 5;
			ep.duration = 3453;
			ep.status = Ep.IN_SERVER;
			ep.isNew = false;
			ep.description = "酱油冲击播第33期开播，本期节目与大家分享川井宪次的一些配乐作品，大气磅礴，超燃，强烈建议工作时收听，给你进击的动力。\n\n主播：GD\n\n曲目列表：\n1. 百禽 (めざめの方舟)\n2. Log In (Avalon)\n3. 00 Gundam (Gundam 00)\n4. Strike (Gundam 00)\n5. Apocalypse (Apocalypse: The Second World War)\n6. Trap (Apocalypse: The Second World War)\n7. Offensive (Apocalypse: The Second World War)\n8. 墨攻 (墨攻)\n9. 一代宗师 (叶问)\n10. 七剑战歌 (七剑)\n11. 傀儡謡-陽炎は黄泉に待たむと (攻壳机动队2: Innocence)\n";
			ep.url = AppMain.debugServer + "podcasts/jyshock033.mp3";
			ep.coverUrl = AppMain.debugServer + "images/jyshock033.jpg";
			ep.coverThumbnailUrl = AppMain.debugServer + "images/jyshock033_200x200.jpg";
			ep.publishDate = new Date();
			break;
		case 8:
			ep.id = 8;
			ep.title = "酱油播动拳-VOL.026-冯导的春晚";
			ep.star = 4;
			ep.duration = 3637;
			ep.status = Ep.IN_SERVER;
			ep.isNew = false;
			ep.description = "酱油播动拳第26期开播，冯导竟然要去执导春晚了，这一定要说说，后半par有深度讨论与强力吐槽，相关影迷请注意防护。\n\n主播：GD、晓星、郑博、open";
			ep.url = AppMain.debugServer + "podcasts/jyhadouken026.mp3";
			ep.coverUrl = AppMain.debugServer + "images/jyhadouken026.jpg";
			ep.coverThumbnailUrl = AppMain.debugServer + "images/jyhadouken026_200x200.jpg";
			ep.publishDate = new Date();
			break;
		case 9:
			ep.id = 9;
			ep.title = "酱油播动拳-VOL.025-兵败合肥";
			ep.star = 5;
			ep.duration = 2414;
			ep.status = Ep.IN_SERVER;
			ep.isNew = false;
			ep.description = "酱油播动拳第25期开播，6月15日，国足持续走低再创新低，同是也给我们提供了诸多槽点，节目中间有伪球迷出没，相关人员做好防护。\n\n主播：open、段超、GD";
			ep.url = AppMain.debugServer + "podcasts/jyhadouken025.mp3";
			ep.coverUrl = AppMain.debugServer + "images/jyhadouken025.jpg";
			ep.coverThumbnailUrl = AppMain.debugServer + "images/jyhadouken025_200x200.jpg";
			ep.publishDate = new Date();
			break;
		case 10:
			ep.id = 10;
			ep.title = "酱油冲击播-VOL.032-猫";
			ep.star = 5;
			ep.duration = 1703;
			ep.status = Ep.IN_SERVER;
			ep.isNew = false;
			ep.description = "酱油冲击播第32期开播，猫是很常见的动物，是人类的朋友，而音乐中的猫会给人什么样的感觉，这期节目带你一起感受一下。\n\n主播：GD\n\n歌曲列表：\n1. つじあやの – 風になる\n2. Cat Power – The Greatest\n3. Cat Power – After It All\n4. Porcupine Tree – Nine Cats\n5. The Beatles – Three Cool Cats\n6. Ry Cooder – Three Cool Cats\n7. Anoice – Cat In the Rain\n8. My Cats A Stargazer – …Evening Air…";
			ep.url = AppMain.debugServer + "podcasts/jyshock032.mp3";
			ep.coverUrl = AppMain.debugServer + "images/jyshock032.jpg";
			ep.coverThumbnailUrl = AppMain.debugServer + "images/jyshock032_200x200.jpg";
			ep.publishDate = new Date();
			break;
		case 11:
			ep.id = 11;
			ep.title = "酱油冲击播-VOL.031-庆六一";
			ep.star = 5;
			ep.duration = 3690;
			ep.status = Ep.IN_SERVER;
			ep.isNew = false;
			ep.description = "酱油冲击播第31期开播，六一儿童节就要到了，祝大家节日快乐，这是一期特别节目，跟大家一起回忆美好的童年。\n\n主播：GD\n\n热烈庆祝六一儿童节！！好好学习，天天向上！！";
			ep.url = AppMain.debugServer + "podcasts/jyshock031.mp3";
			ep.coverUrl = AppMain.debugServer + "images/jyshock031.jpg";
			ep.coverThumbnailUrl = AppMain.debugServer + "images/jyshock031_200x200.jpg";
			ep.publishDate = new Date();
			break;
		}
		
		return ep;
		
	}

	
	
}
