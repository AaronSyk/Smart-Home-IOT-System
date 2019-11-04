/*
 * MainFrame.java
 *
 * Created on 2016.8.19
 */

package com.yang.serialport.ui;

import gnu.io.SerialPort;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.lang.Object;
import java.text.Format;
import java.text.SimpleDateFormat;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Image;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ImageIcon;

import com.yang.serialport.exception.NoSuchPort;
import com.yang.serialport.exception.NotASerialPort;
import com.yang.serialport.exception.PortInUse;
import com.yang.serialport.exception.SendDataToSerialPortFailure;
import com.yang.serialport.exception.SerialPortOutputStreamCloseFailure;
import com.yang.serialport.exception.SerialPortParameterFailure;
import com.yang.serialport.exception.TooManyListeners;
import com.yang.serialport.manage.SerialPortManager;
import com.yang.serialport.utils.ByteUtils;
import com.yang.serialport.utils.ShowUtils;
import java.util.Scanner;
import java.util.*;
import java.lang.String;
import java.sql.*;
import java.awt.Color;
import java.awt.Font;
public class MainFrame extends JFrame {

	/**
	 * 程序界面宽度
	 */
	public static final int WIDTH = 500;

	/**
	 * 程序界面高度
	 */
	public static final int HEIGHT = 460;

	private JTextArea dataView = new JTextArea();
	private JScrollPane scrollDataView = new JScrollPane(dataView);

	
	
	//--------------温度文本区---------------
		private JPanel wendu = new JPanel();
		private JComboBox commChoice = new JComboBox();
		private JComboBox baudrateChoice = new JComboBox();
		private JTextField dataInput1 = new JTextField();  //上限
		private JTextField dataInput2 = new JTextField();   //下限
		private JLabel shangxianLabel1=new JLabel(); //上限标签
		private JLabel xiaxianLabel1=new JLabel();   //下限标签
	          private JButton consendData1 = new JButton("开");
	        private JButton consendData2 = new JButton("关");
		private JLabel condition =new JLabel(); //空调标签
	

		//--------------湿度文本区---------------
		private JPanel shidu = new JPanel();
		private JTextField dataInput3 = new JTextField();   //上限
		private JTextField dataInput4 = new JTextField();    //下限
		private JLabel shangxianLabel2=new JLabel();   //上限标签
		private JLabel xiaxianLabel2=new JLabel();    //下限标签
	    private JButton humsendData1 = new JButton("开");
	    private JButton humsendData2 = new JButton("关");
		private JLabel hum =new JLabel(); //空调标签
		
		
//--------------光敏文本区---------------
		private JPanel guangmin = new JPanel();
		private JButton guangsendData1 = new JButton("开");
		private JButton guangsendData2 = new JButton("关");
		private JLabel guang =new JLabel(); //空调标签
		
		private JButton openseriPort = new JButton("智能家居系统开启");
		
		private List<String> commList = null;
		private SerialPort serialport;
	public MainFrame() {
		initView();
		initComponents();
		actionListener();
		initData();
	}
	// ----------------------------------------

	void writefile(double i, double h) throws IOException {
		// 写入文本文档
		FileWriter fileWriter = new FileWriter("E://Result.txt", true);
		// true表示在fw对文件再次写入时，会在该文件的结尾续写，并不会覆盖掉
		// 创建对应位置文本文档
		fileWriter.write("温度"+String.valueOf(i) + "," + "湿度"+String.valueOf(h) + "\t");
		// 写入内容
		fileWriter.flush();// 刷新
		fileWriter.close();// 关闭
	}

	// ---------------------------------------
	private void initView() {
		//设置背景图片
		String path = "pic5.jpg";//0-10可以变，我觉得五好看
		// 背景图片
		ImageIcon background = new ImageIcon(path);
		// 把背景图片显示在一个标签里面
		background.setImage(background.getImage().getScaledInstance(500,460,Image.SCALE_DEFAULT));
		JLabel label = new JLabel(background);
		// 把标签的大小位置设置为图片刚好填充整个面板
		label.setBounds(0, 0, 500, 460);
		// 把内容窗格转化为JPanel，否则不能用方法setOpaque()来使内容窗格透明
		JPanel imagePanel = (JPanel) this.getContentPane();
		imagePanel.setOpaque(false);
		// 把背景图片添加到分层窗格的最底层作为背景
		this.getLayeredPane().add(label, new Integer(Integer.MIN_VALUE));	
		
		// 关闭程序
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		// 禁止窗口最大化
		setResizable(false);

		// 设置程序窗口居中显示
		Point p = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
		setBounds(p.x - WIDTH / 2, p.y - HEIGHT / 2, WIDTH, HEIGHT);
		this.setLayout(null);

		setTitle("智能家居系统");

		
	}

	//-----------------initComponents()函数-----------------------
	private void initComponents() {
        dataView.setFocusable(false);
        dataView.setBackground(new Color(209,224,239));
	    scrollDataView.setBounds(10, 10, 475, 100);
        add(scrollDataView);

		wendu.setBorder(BorderFactory.createTitledBorder("温度"));
		wendu.setBackground(new Color(90,163,197));
		wendu.setBounds(30, 120, 210, 120);
		wendu.setLayout(null);
		add(wendu);
		dataInput1.setBounds(100, 15, 100, 20);
		wendu.add(dataInput1);
		dataInput2.setBounds(100, 50, 100, 20);
		wendu.add(dataInput2);
		shangxianLabel1.setText("上限");
		shangxianLabel1.setFont(new Font ("楷体",Font.PLAIN,13));
		shangxianLabel1.setBounds(50,15,80,20);
		wendu.add(shangxianLabel1);
		xiaxianLabel1.setText("下限");
		xiaxianLabel1.setFont(new Font ("楷体",Font.PLAIN,13));
		xiaxianLabel1.setBounds(50,50,80,20);
		wendu.add(xiaxianLabel1);

        condition.setText("空调");
        condition.setFont(new Font ("楷体",Font.BOLD,13));
        condition.setBounds(50,85,40,20);
        wendu.add(condition);
		consendData1.setFocusable(false);
		consendData1.setFont(new Font ("楷体",Font.BOLD,13));
		consendData1.setBounds(100,85, 50, 20);
		wendu.add(consendData1);
		consendData2.setFocusable(false);
		consendData2.setFont(new Font ("楷体",Font.BOLD,13));
		consendData2.setBounds(150,85, 50, 20);
		wendu.add(consendData2);

		shidu.setBorder(BorderFactory.createTitledBorder("湿度"));
		shidu.setBackground(new Color(90,163,197));
		shidu.setBounds(250, 120, 210, 120);
		shidu.setLayout(null);
		add(shidu);
		dataInput3.setBounds(100, 15, 100, 20);
		shidu.add(dataInput3);
		dataInput4.setBounds(100, 50, 100, 20);
		shidu.add(dataInput4);
		shangxianLabel2.setText("上限");
		shangxianLabel2.setFont(new Font ("楷体",Font.PLAIN,13));
		shangxianLabel2.setBounds(50,15,80,20);
		shidu.add(shangxianLabel2);
		xiaxianLabel2.setText("下限");
		xiaxianLabel2.setFont(new Font ("楷体",Font.PLAIN,13));
		xiaxianLabel2.setBounds(50,50,80,20);
		shidu.add(xiaxianLabel2);
        hum.setText("加湿器");
        hum.setFont(new Font ("楷体",Font.BOLD,13));
        hum.setBounds(50,85,50,20);
        shidu.add(hum);
        humsendData1.setFocusable(false);
        humsendData1.setFont(new Font ("楷体",Font.BOLD,13));
        humsendData1.setBounds(100,85,50,20);
        shidu.add(humsendData1);
        humsendData2.setFocusable(false);
        humsendData2.setFont(new Font ("楷体",Font.BOLD,13));
        humsendData2.setBounds(150,85,50,20);
        shidu.add(humsendData2);

        guangmin.setBorder(BorderFactory.createTitledBorder("灯光控制"));
        guangmin.setBackground(new Color(90,163,197));
        guangmin.setBounds(30,250, 435, 80);
        guangmin.setLayout(null);
		add(guangmin);
	    guangsendData1.setFocusable(false);
	    guangsendData1.setFont(new Font ("楷体",Font.BOLD,13));
	    guangsendData1.setBounds(170,30,80,20);
        guangmin.add(guangsendData1);
	    guangsendData2.setFocusable(false);
	    guangsendData2.setFont(new Font ("楷体",Font.BOLD,13));
	    guangsendData2.setBounds(270,30,80,20);
        guangmin.add(guangsendData2);
		guang.setText("灯光");
		guang.setFont(new Font ("楷体",Font.BOLD,13));
		guang.setBounds(70,20,80,30);
		guangmin.add(guang);


		openseriPort.setFocusable(false);
		openseriPort.setFont(new Font ("楷体",Font.BOLD,13));//按钮字体的变化
		//openseriPort.setBackground(Color.black);
		openseriPort.setBounds(150, 360, 200, 40);
		openseriPort.setLayout(null);
		add(openseriPort);
		
}
	

	
	// 串口设置

	@SuppressWarnings("unchecked")
	private void initData() {
		commList = SerialPortManager.findPort();
		// 检查是否有可用串口，有则加入选项中
		if (commList == null || commList.size() < 1) {
			ShowUtils.warningMessage("没有搜索到有效串口！");
		} else {
			for (String s : commList) {
				commChoice.addItem(s);
				// 将项目添加到项目列表,只有JComboBox使用可变数据模型时，此方法才有效
			}
		}

		baudrateChoice.addItem("115200");

		// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	}


	private void actionListener() {
		openseriPort.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if ("智能家居系统开启".equals(openseriPort.getText())
						&& serialport == null) {
					oSerialPort(e);
				} else {
					cSerialPort(e);
				}
			}
		});

		consendData1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				consendData1(e);
			}
		});
		consendData2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				consendData2(e);
			}
		});
		humsendData1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				humsendData1(e);
			}
		});
		humsendData2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				humsendData2(e);
			}
		});
		guangsendData1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				guangsendData1(e);
			}
		});
		guangsendData2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				guangsendData2(e);
			}
		});
	}


	/**
	 * 打开串口
	 * 
	 * @param evt
	 *            点击事件
	 */
	private void oSerialPort(java.awt.event.ActionEvent evt) {
		// 获取串口名称
		String commName = (String) commChoice.getSelectedItem();
		// 获取波特率
		int baudrate = 115200;
		String bps = (String) baudrateChoice.getSelectedItem();
		baudrate = Integer.parseInt(bps);

		// 检查串口名称是否获取正确
		if (commName == null || commName.equals("")) {
			ShowUtils.warningMessage("没有搜索到有效串口！");
		} else {
			try {
				// 打开串口
				serialport = SerialPortManager.openPort(commName, baudrate);
				if (serialport != null) {
					dataView.setText("实时温度与湿度：" + "\r\n");
					openseriPort.setText("关闭智能家居系统");
				}
			} catch (SerialPortParameterFailure e) {
				e.printStackTrace();
			} catch (NotASerialPort e) {
				e.printStackTrace();
			} catch (NoSuchPort e) {
				e.printStackTrace();
			} catch (PortInUse e) {
				e.printStackTrace();
				ShowUtils.warningMessage("串口被占用！");
			}
		}

		try {
			SerialPortManager.addListener(serialport, new SerialListener());
		} catch (TooManyListeners e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭串口
	 * 
	 * @param evt
	 *            点击事件
	 */
	private void cSerialPort(java.awt.event.ActionEvent evt) {
		SerialPortManager.closePort(serialport);
		dataView.setText("串口已关闭" + "\r\n");
		openseriPort.setText("开启智能家居系统");
	}

	public static double tem;
	public static int k=0,j=0,d=0;;

	/**
	 * 发送数据
	 * 
	 * @param evt
	 *            点击事件
	 */
	private void consendData1(java.awt.event.ActionEvent evt) {
		String data = "3A00010B013123";// 响应打开电机点击的事件后，发送打开电机的命令
		try {
			k=1;
			for(int i=0;i<500;i++)
				SerialPortManager.sendToPort(serialport, ByteUtils.hexStr2Byte(data));// 发送字节命令
			dataView.append("■■■开空调■■■"+"\n");
		} catch (SendDataToSerialPortFailure e) {
			e.printStackTrace();
		} catch (SerialPortOutputStreamCloseFailure e) {
			e.printStackTrace();
		}
	}

	private void consendData2(java.awt.event.ActionEvent evt) {
		String data = "3A00010B003023";// 响应点击关闭点击的事件后，发送关闭电机的命令
		try {
			k=0;
			for(int i=0;i<500;i++)
			SerialPortManager.sendToPort(serialport, ByteUtils.hexStr2Byte(data));
			dataView.append("关空调"+"\n");
		} catch (SendDataToSerialPortFailure e) {
			e.printStackTrace();
		} catch (SerialPortOutputStreamCloseFailure e) {
			e.printStackTrace();
		}
	}
	


	private void humsendData1(java.awt.event.ActionEvent evt) {
		String data = "3A00010B003023";//响应点击关闭点击的事件后，发送关闭电机的命令
		try {
			j=1;
			for(int i=0;i<500;i++)
			SerialPortManager.sendToPort(serialport,
					ByteUtils.hexStr2Byte(data));
			dataView.append("♒♒开加湿器♒♒"+"\n");
		} catch (SendDataToSerialPortFailure e) {
			e.printStackTrace();
		} catch (SerialPortOutputStreamCloseFailure e) {
			e.printStackTrace();
		}
	}
	private void humsendData2(java.awt.event.ActionEvent evt) {
		String data = "3A00010B003023";//响应点击关闭点击的事件后，发送关闭电机的命令
		try {
			j=0;
			for(int i=0;i<500;i++)
			SerialPortManager.sendToPort(serialport,
					ByteUtils.hexStr2Byte(data));
			dataView.append("关加湿器"+"\n");
		} catch (SendDataToSerialPortFailure e) {
			e.printStackTrace();
		} catch (SerialPortOutputStreamCloseFailure e) {
			e.printStackTrace();
		}
	}
	
	
	
	private void guangsendData1(java.awt.event.ActionEvent evt) {
		String data = "3A00010A003023";//响应点击关闭点击的事件后，发送打开灯光的命令
		try {
			d=1;
			for(int i=0;i<500;i++)
			SerialPortManager.sendToPort(serialport,
					ByteUtils.hexStr2Byte(data));
			dataView.append("☀☀☀开灯☀☀☀"+"\n");
		} catch (SendDataToSerialPortFailure e) {
			e.printStackTrace();
		} catch (SerialPortOutputStreamCloseFailure e) {
			e.printStackTrace();
		}
	}
	private void guangsendData2(java.awt.event.ActionEvent evt) {
		String data = "3A00010A013123";//响应点击关闭点击的事件后，发送关闭灯光的命令
		try {
			d=0;
			for(int i=0;i<500;i++)
			SerialPortManager.sendToPort(serialport,
					ByteUtils.hexStr2Byte(data));
			dataView.append("☼☼☼关灯☼☼☼"+"\n");
		} catch (SendDataToSerialPortFailure e) {
			e.printStackTrace();
		} catch (SerialPortOutputStreamCloseFailure e) {
			e.printStackTrace();
		}
	}
	
	public static void Test(double t, double h) throws Exception {
		 Connection con = null;
	try {// 建立连接
		 Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver"); 
		 //动态加载数据库驱动
		 con =DriverManager.getConnection("jdbc:sqlserver://localhost:1433;DatabaseName=test","sa", "1");// 使用DriverManager获取数据库连接
		 if (con != null)
		 System.out.println("success");
		 else
		 System.out.println("fail"); // 检验连接是否正确
		 Date date=new Date();
		 SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");//转换为指定格式的文本HH:mm:ss
		 String time=sdf.format(date);
		 System.out.println(time);
		 
		 String str1 = "insert into zhineng values('"+ time+"'," + t + "," + h + ")";
		 Statement st = con.createStatement();
		 int c = st.executeUpdate(str1);
		 System.out.println(c);
		  con.close();
		 } catch (ClassNotFoundException e) {
		 e.printStackTrace(); // public void printStackTrace()将此
		 // throwable 及其追踪输出至标准错误流
		 }
		}
	
	private class SerialListener implements SerialPortEventListener {
		/**
		 * 处理监控到的串口事件
		 */
	
		public void serialEvent(SerialPortEvent serialPortEvent) {

			switch (serialPortEvent.getEventType()) {

			case SerialPortEvent.BI: // 10 通讯中断
				ShowUtils.errorMessage("与串口设备通讯中断");
				break;

			case SerialPortEvent.OE: // 7 溢位（溢出）错误

			case SerialPortEvent.FE: // 9 帧错误

			case SerialPortEvent.PE: // 8 奇偶校验错误

			case SerialPortEvent.CD: // 6 载波检测

			case SerialPortEvent.CTS: // 3 清除待发送数据

			case SerialPortEvent.DSR: // 4 待发送数据准备好了

			case SerialPortEvent.RI: // 5 振铃指示

			case SerialPortEvent.OUTPUT_BUFFER_EMPTY: // 2 输出缓冲区已清空
				break;

			case SerialPortEvent.DATA_AVAILABLE: // 1 串口存在可用数据
				byte[] data = null;
				
				//FileWriter fileWriter=new FileWriter("d://Result.txt");
				
		
				try {
					if (serialport == null) {
						ShowUtils.errorMessage("串口对象为空！监听失败！");
					} else {
						// 读取串口数据
						double ws,wx,ss,sx;
						ws=Integer.parseInt(dataInput1.getText().toString());
						wx=Integer.parseInt(dataInput2.getText().toString());
						ss=Integer.parseInt(dataInput3.getText().toString());
						sx=Integer.parseInt(dataInput4.getText().toString());
						data = SerialPortManager.readFromPort(serialport);
						//从串口读取数据
						double h=0;
						double ad=0;
					    int flag=0;
					  
					    int light=0;
					    int kongtiao=0;
					    int jiashiqi=0;
					    
					    
					    
						//对温湿度传感器的处理
//						if(data[3]==3){
							System.out.println("温湿度\n");  
						
							if((data[5]>0)&&(data[6]!=0))
						   {
//							double q=data[5];
//							if(data[6]<0)
//								{
//								q=data[6]+2*2*2*2*2*2*2*2;  //将16进制的负补码数据换为正的10进制数
//								}
							tem=(data[4]*256)/100;     //计算求出10进制表示的温度值
//							double t=data[7];
//							if(data[7]<0)
//							{
//							t=data[67]+2*2*2*2*2*2*2*2;    //将16进制的负补码数据换为正的10进制数
//							}
//							double p=data[8];
//							if(data[8]<0)
//							{
//							p=data[8]+2*2*2*2*2*2*2*2;   //将16进制的负补码数据换为正的10进制数
//							}
							h=(data[5]*256)/100;  //计算将湿度值换位10进制
							//new SerialListener().writefile(tem,h);
							//FileWriter fileWriter=new FileWriter("d://Result.txt");					    
						
							new MainFrame().writefile(tem,h);
							
							  //try{
								Test(tem,h);
							//	}catch(Exception ex)
								///{System.out.println("fail to sqlserver");}
							
							if(tem>ws||tem<wx)   //对读取的数据与上下限值进行比较
							{dataView.append("开空调"+"\n");
								for(int i=0;i<1000;i++)
									SerialPortManager.sendToPort(serialport,     //温度超出范围发送启动电机的命令
											ByteUtils.hexStr2Byte("3A00010B013123"));
									
								for(int i=0;i<1000;i++)
								SerialPortManager.sendToPort(serialport,     //温度超出范围发送启动电机的命令
										ByteUtils.hexStr2Byte("3A00010B013123"));
								
								
							
							}
							else if(h<sx){
								for(int i=0;i<900;i++)
									SerialPortManager.sendToPort(serialport,     //温度超出范围发送启动电机的命令
											ByteUtils.hexStr2Byte("3A00010B013123"));
								for(int i=0;i<1000;i++)
									SerialPortManager.sendToPort(serialport,     //超出范围发送启动电机的命令
											ByteUtils.hexStr2Byte("3A00010B013123"));
								
							
									
								dataView.append("开加湿器"+"\n");
								
							}else {
								if(tem<ws&&tem>wx) 
								for(int i=0;i<3000;i++)
									SerialPortManager.sendToPort(serialport,    //温度回到范围内发送关闭电机的命令
										ByteUtils.hexStr2Byte("3A00010B003023"));
								if(k==0&&j==0)
									for(int i=0;i<3000;i++)
									SerialPortManager.sendToPort(serialport,    //温度回到范围内发送关闭电机的命令
										ByteUtils.hexStr2Byte("3A00010B003023"));
								
								if(h>sx)
								for(int i=0;i<3000;i++)
								SerialPortManager.sendToPort(serialport,    //温度回到范围内发送关闭电机的命令
									ByteUtils.hexStr2Byte("3A00010B003023"));
								
		
							}
						}
						dataView.append(" 温度："+tem+"             "+"湿度："+h+"%\r\n");   //输出相应的经过处理的数据
                        System.out.println(" 温度："+tem+"             "+"湿度："+h+"%\r\n");
                    
                      
				
//						}//温湿度
						
						
						
						//对红外热释电传感器处理
//						else if(data[3]==8){
//							System.out.println("红外\n");
//							if(flag==0){
//								if(data[5]==1){
//							
//								
//									dataView.append("有人进入，红灯警示"+"\n");
//								  for(int i=0;i<1000;i++)
//									SerialPortManager.sendToPort(serialport,     //超出范围发送启动led1的命令
//										ByteUtils.hexStr2Byte("CCEE01090100000000000000000000FF"));
//								
//									flag=1;
//						
//								
//								}else {
//									for(int i=0;i<500;i++)
//									SerialPortManager.sendToPort(serialport,     //超出范围发送关闭led1的命令
//										ByteUtils.hexStr2Byte("CCEE01090200000000000000000000FF"));
//								}
//							
//							}else if(flag==1){
//								if(data[5]==0){
//									flag=0;
//									for(int i=0;i<1000;i++)
//									SerialPortManager.sendToPort(serialport,     //超出范围发送关闭led1的命令
//										ByteUtils.hexStr2Byte("CCEE01090200000000000000000000FF"));
//							
//								}else {
//									for(int i=0;i<500;i++)
//									SerialPortManager.sendToPort(serialport,     //超出范围发送关闭led1的命令
//											ByteUtils.hexStr2Byte("CCEE01090100000000000000000000FF"));
//									}
//							}
//						}//red
						//对光敏传感器的处理
//						 if(data[3]==2){
							double adlight=350;	
							 System.out.println(" 光敏：");
						if((data[8]>=0)&&(data[9]>=0))
						{
							double q=data[9];
//							if(data[6]<0)
//								{q=data[6]+2*2*2*2*2*2*2*2;  //将16进制的负补码数据换为正的10进制数
//								
//								}
							ad=(data[8]*256+q);     //计算求出10进制表示的温度值
							dataView.append(" 光敏："+ad+"\r\n");   //输出相应的经过处理的数据
	                        System.out.println(" 光敏："+ad+"\r\n");
							if(ad<adlight&&ad!=0)   //对读取的数据值进行比较
							{   
								for(int i=0;i<1000;i++)
								SerialPortManager.sendToPort(serialport,     //温度超出范围发送开led4的命令
										ByteUtils.hexStr2Byte("3A00010A013023"));
								if(light==0){
								dataView.append("光强过低，开灯"+"\n");}
								light=1;
							}else {
								if(d!=0)
								for(int i=0;i<500;i++)
								SerialPortManager.sendToPort(serialport,    //温度回到范围内发送关闭led4的命令
									ByteUtils.hexStr2Byte("3A00010A013123"));
								light=0;
							}	
							
						}
						
//						}//光敏
						  
							//对光敏传感器的处理
//						else if(data[3]==2){
//							System.out.println(" 光敏：");
//							double adlight=350;	
//							if((data[5]>0)&&(data[6]!=0))
//							{
//								double q=data[6];
//								if(data[6]<0)
//									{q=data[6]+2*2*2*2*2*2*2*2;  //将16进制的负补码数据换为正的10进制数
//									
//									}
//								ad=(data[5]*256+q);     //计算求出10进制表示的温度值
//								
//								if(ad<adlight&&ad!=0)   //对读取的数据值进行比较
//								{   
//									for(int i=0;i<1000;i++)
//									SerialPortManager.sendToPort(serialport,     //温度超出范围发送开led4的命令
//											ByteUtils.hexStr2Byte("CCEE01090700000000000000000000FF"));
//									if(light==0){
//									dataView.append("光强过低，开灯"+"\n");}
//									light=1;
//								}else {
//									if(d!=0)
//									for(int i=0;i<500;i++)
//									SerialPortManager.sendToPort(serialport,    //温度回到范围内发送关闭led4的命令
//										ByteUtils.hexStr2Byte("CCEE01090800000000000000000000FF"));
//									light=0;
//								}	
//								dataView.append(" 光敏："+ad+"\r\n");   //输出相应的经过处理的数据
//		                        System.out.println(" 光敏："+ad+"\r\n");
//							}
//							
//							}//光敏
//						else{
//							
//							if(light==1){
//								for(int i=0;i<500;i++)
//								SerialPortManager.sendToPort(serialport,     //温度超出范围发送开led4的命令
//										ByteUtils.hexStr2Byte("3A00010A013023"));
//							}else if(light==0&&d==0){
//								for(int i=0;i<100;i++)
//									SerialPortManager.sendToPort(serialport,     //温度超出范围发送close led4的命令
//											ByteUtils.hexStr2Byte("3A00010A013123"));
//							}
//							if(flag==1){
//								for(int i=0;i<500;i++)
//								SerialPortManager.sendToPort(serialport,     //超出范围发送启动led1的命令
//									ByteUtils.hexStr2Byte("CCEE01090100000000000000000000FF"));
//							}else if(flag==0){
//								for(int i=0;i<100;i++)
//									SerialPortManager.sendToPort(serialport,     //超出范围发送close led1的命令
//										ByteUtils.hexStr2Byte("CCEE01090200000000000000000000FF"));
//								
//							}
//							if(kongtiao==1){
//								for(int i=0;i<500;i++)
//								{SerialPortManager.sendToPort(serialport,     //温度超出范围发送启动电机的命令
//										ByteUtils.hexStr2Byte("CCEE01090900000000000000000000FF"));
//								SerialPortManager.sendToPort(serialport,     //温度超出范围发送启动电机的命令
//										ByteUtils.hexStr2Byte("CCEE01090300000000000000000000FF"));
//								}
//							}else if(kongtiao==0&&k==0){
//								for(int i=0;i<100;i++)
//								{
//									if(jiashiqi!=1)
//										SerialPortManager.sendToPort(serialport,     //温度超出范围发送启动电机的命令
//										    ByteUtils.hexStr2Byte("CCEE01090B00000000000000000000FF"));
//								    SerialPortManager.sendToPort(serialport,     //温度超出范围发送启动电机的命令
//										ByteUtils.hexStr2Byte("CCEE01090400000000000000000000FF"));
//								}
//							}
//							if(jiashiqi==1){
//								for(int i=0;i<500;i++)
//								{SerialPortManager.sendToPort(serialport,     //温度超出范围发送启动电机的命令
//										ByteUtils.hexStr2Byte("CCEE01090900000000000000000000FF"));
//								SerialPortManager.sendToPort(serialport,     //温度超出范围发送启动电机的命令
//										ByteUtils.hexStr2Byte("CCEE01090500000000000000000000FF"));
//								}
//								
//							}else if(jiashiqi==0&&j==0){
//								for(int i=0;i<100;i++)
//								{
//									if(kongtiao!=1)
//										SerialPortManager.sendToPort(serialport,     //温度超出范围发送启动电机的命令
//										    ByteUtils.hexStr2Byte("CCEE01090B00000000000000000000FF"));
//								    SerialPortManager.sendToPort(serialport,     //温度超出范围发送启动电机的命令
//										ByteUtils.hexStr2Byte("CCEE01090600000000000000000000FF"));
//								}
//								
//							}
							
//						}
							
							
							
					}//else
				}//try
					catch (Exception e) {
					ShowUtils.errorMessage(e.toString());
					// 发生读取错误时显示错误信息后退出系统
					System.exit(0);
				}
				break;
			}//switch
		}//se
	}// sl

	

		public static DataWindow dw = new DataWindow();

		public static void main(String args[]) {

			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					new MainFrame().setVisible(true);// 显示MainFrame窗口
				}
			});// 将来自于底层同位体类和应用程序类的事件列入队列。它封装了异步事件指派机制，从队列中提取事件

			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					dw.setVisible(true);// 显示DataWindow窗口
				}
			});

			Thread thread = new Thread() {
				public void run() {
					while (true) {
						Long t = System.currentTimeMillis();// 以毫秒为单位返回当前时间
						int v = (int) tem;
						if (v > 0 && v < 100) {
							dw.addData(t, v);
						}
						dw.repaint();
						try {
							Thread.sleep(300L);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			};

			thread.run();

		}
}