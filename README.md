CodeVS 5.0 Manual Play
======================

CODE VS5.0 �� �v���Z�b�gAI���Ƃ̃��[�J���ΐ��AI����Ȃ��}�j���A���őΐ킷��  

�� CODE VS5.0 �̏ڍׂ� �� https://codevs.jp/

[[https://github.com/neetsdkasu/codevs5.0/wiki/codevs5manualplay1.png]]  



�\�[�X�� Main.java �� Player.java ���R���p�C������Ƃ�������N���X�����������  

`Main.class` ... CODE VS 5.0 �̃N���C�A���g�Ŏ��s������ق��̃N���X  
`Player.class` ... �}�j���A�����삷�邽�߂�UI�A�ΐ���n�߂�O��Player.class��P�ƋN��������K�v������  




�R���p�C���Ǝ��s
----------------
Java8 ���K�v�ł���  

Main.java��Player.java�� C:\CodeVS5\ �ɔz�u�����Ƃ����  

�R���p�C����  

	cd C:\CodeVS5
	if not exist classes mkdir classes
	javac -d ".\classes" Main.java Player.java


class�̂܂܎��s������ꍇ��  

�܂�UI�N�� (���N���C�A���g�őΐ�n�߂�O�ɋN������ɂ�)  

	cd C:\CodeVS5
	java -cp classes Player

CODE VS 5.0 �N���C�A���g�̂ق��ɂ� 

	java -cp "C:\CodeVS5\classes" Main

�Ə����΂���  


JAR�Ƀp�b�N����ꍇ��  

	cd C:\CodeVS5
	jar cvf ManualPlay.jar -C classes .

���Fjar�R�}���h�̍s���Ƀh�b�g�����̂ŖY�ꂸ��  

JAR�����UI�N���� (���N���C�A���g�őΐ�n�߂�O�ɋN������ɂ�)  

	cd C:\CodeVS5
	java -cp ManualPlay.jar Player

CODE VS5.0 �̃N���C�A���g�̂ق��ɂ�  

	java -cp "C:\CodeVS5\ManualPlay.jar" Main

�Ə����΂���  




������@
--------

`W` ���ǂ�\��  
`O` ���΂�\��  
`S` ���j���W���\�E����\��  
`d` ������\��  
`@` ���E�҂�\��(�Ԃ�ID-0�A�}�[���^��ID-1) 

 * �E�҂̈ړ�  
�E�҂��N���b�N����ƃN���b�N�����E�҂̈ړ����[�h�ɂȂ���  
�E�҂̈ړ���1�����ړ�����N���b�N����  
2����(��������3����)�N���b�N����܂ňړ����[�h��Ԃ̂܂܂ł���  
���A�ړ����̃L�����Z��(���Ȃ���)�͂ł��Ȃ�   

 * �E�p  
	+ ���� ... �E�҂̈ړ��O��Speed Up�̃{�^��������  
	+ ���� ... Drop Rock�{�^������������ɗ��Ƃ��ꏊ���N���b�N����  
	+ ���� ... Thunder�{�^������������ɗ��Ƃ��ꏊ���N���b�N����  
	+ ���g ... Dummy�{�^������������ɏo�����������ꏊ���N���b�N����  
	+ ��a ... Turn Cut�{�^������������ɉ�a������E�҂��N���b�N����  

�S�Ă̓��͂��I������� OK �{�^��������  

	
[[https://github.com/neetsdkasu/codevs5.0/wiki/codevs5manualplay2.png]]  
