CodeVS 5.0 Manual Play
======================

CODE VS5.0 で プリセットAI等とのローカル対戦をAIじゃなくマニュアルで対戦する  

※ CODE VS5.0 の詳細は → https://codevs.jp/

[[https://github.com/neetsdkasu/codevs5.0/wiki/codevs5manualplay1.png]]  



ソースの Main.java と Player.java をコンパイルするとたくさんクラスが生成される  

`Main.class` ... CODE VS 5.0 のクライアントで実行させるほうのクラス  
`Player.class` ... マニュアル操作するためのUI、対戦を始める前にPlayer.classを単独起動させる必要がある  




コンパイルと実行
----------------
Java8 が必要である  

Main.javaとPlayer.javaを C:\CodeVS5\ に配置したとすると  

コンパイルは  

	cd C:\CodeVS5
	if not exist classes mkdir classes
	javac -d ".\classes" Main.java Player.java


classのまま実行させる場合は  

まずUI起動 (※クライアントで対戦始める前に起動するにょ)  

	cd C:\CodeVS5
	java -cp classes Player

CODE VS 5.0 クライアントのほうには 

	java -cp "C:\CodeVS5\classes" Main

と書けばおｋ  


JARにパックする場合は  

	cd C:\CodeVS5
	jar cvf ManualPlay.jar -C classes .

注：jarコマンドの行末にドット書くので忘れずに  

JARからのUI起動は (※クライアントで対戦始める前に起動するにょ)  

	cd C:\CodeVS5
	java -cp ManualPlay.jar Player

CODE VS5.0 のクライアントのほうには  

	java -cp "C:\CodeVS5\ManualPlay.jar" Main

と書けばおｋ  




操作方法
--------

`W` が壁を表す  
`O` が石を表す  
`S` がニンジャソウルを表す  
`d` が犬を表す  
`@` が忍者を表す(赤がID-0、マゼンタがID-1) 

 * 忍者の移動  
忍者をクリックするとクリックした忍者の移動モードになって  
忍者の移動を1歩ずつ移動先をクリックする  
2歩分(超速時は3歩分)クリックするまで移動モード状態のままである  
尚、移動等のキャンセル(やりなおし)はできない   

 * 忍術  
	+ 超速 ... 忍者の移動前にSpeed Upのボタンを押す  
	+ 落石 ... Drop Rockボタンを押した後に落とす場所をクリックする  
	+ 雷撃 ... Thunderボタンを押した後に落とす場所をクリックする  
	+ 分身 ... Dummyボタンを押した後に出現させたい場所をクリックする  
	+ 回斬 ... Turn Cutボタンを押した後に回斬をする忍者をクリックする  

全ての入力が終わったら OK ボタンを押す  

	
[[https://github.com/neetsdkasu/codevs5.0/wiki/codevs5manualplay2.png]]  
