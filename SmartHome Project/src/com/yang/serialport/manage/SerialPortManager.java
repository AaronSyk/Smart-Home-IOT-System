package com.yang.serialport.manage;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import com.yang.serialport.exception.NoSuchPort;
import com.yang.serialport.exception.NotASerialPort;
import com.yang.serialport.exception.PortInUse;
import com.yang.serialport.exception.ReadDataFromSerialPortFailure;
import com.yang.serialport.exception.SendDataToSerialPortFailure;
import com.yang.serialport.exception.SerialPortInputStreamCloseFailure;
import com.yang.serialport.exception.SerialPortOutputStreamCloseFailure;
import com.yang.serialport.exception.SerialPortParameterFailure;
import com.yang.serialport.exception.TooManyListeners;
import com.yang.serialport.utils.ByteUtils;

public class SerialPortManager {
	//查找所有可用端口 
	@SuppressWarnings("unchecked")
	public static final ArrayList<String> findPort() {
		Enumeration<CommPortIdentifier> portList = CommPortIdentifier
				.getPortIdentifiers(); 
		    //portList取得机器上所有可用端口的列表
		ArrayList<String> portNameList = new ArrayList<String>();
		//取得端口名称列表
		while (portList.hasMoreElements()) {
			String portName = portList.nextElement().getName();
			//portName取得端口名称
			portNameList.add(portName);
			//portList取得机器上所有可用端口“名称”的列表
			
		}
		return portNameList;
	}

	/**
	 * 打开串口
	 * 
	 * @param portName
	 *            端口名称
	 * @param baudrate
	 *            波特率
	 * @return 串口对象
	 * @throws SerialPortParameterFailure
	 *             设置串口参数失败
	 * @throws NotASerialPort
	 *             端口指向设备不是串口类型
	 * @throws NoSuchPort
	 *             没有该端口对应的串口设备
	 * @throws PortInUse
	 *             端口已被占用
	 */
	public static final SerialPort openPort(String portName, int baudrate)
			throws SerialPortParameterFailure, NotASerialPort, NoSuchPort,PortInUse 
		{
		try {
			// 通过端口名识别端口
			CommPortIdentifier portIdentifier = CommPortIdentifier
					.getPortIdentifier(portName);
			// 打开端口，并给端口名字和一个timeout（打开操作的超时时间）
			CommPort commPort = portIdentifier.open(portName, 2000);
			// 判断是不是串口
			if (commPort instanceof SerialPort) {
				//instanceof运算符是用来在运行时指出对象是否是特定类的一个实例
				SerialPort serialPort = (SerialPort) commPort;
				try {
					// 设置一下串口的波特率等参数
					serialPort.setSerialPortParams(baudrate,
							SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
							SerialPort.PARITY_NONE);
					//设置波特率，数据位8，停止1,校验位无
				} catch (UnsupportedCommOperationException e) {
					// 设置串口参数失败
					throw new SerialPortParameterFailure();
					
				}
				return serialPort;
			} else {
				// 不是串口
				throw new NotASerialPort();
			}
		} catch (NoSuchPortException e1) {
			throw new NoSuchPort();
		} catch (PortInUseException e2) {
			throw new PortInUse();
		}
	}

	/**
	 * 关闭串口
	 * 
	 * @param serialport
	 *            待关闭的串口对象
	 */
	public static void closePort(SerialPort serialPort) {
		if (serialPort != null) {
			//关闭串口
			serialPort.close();
			serialPort = null;
		}
	}

	/**
	 * 往串口发送数据
	 * 
	 * @param serialPort
	 *            串口对象
	 * @param order
	 *            待发送数据
	 * @throws SendDataToSerialPortFailure
	 *             向串口发送数据失败
	 * @throws SerialPortOutputStreamCloseFailure
	 *             关闭串口对象的输出流出错
	 */

	public static void sendToPort(SerialPort serialPort, byte[] order)
			throws SendDataToSerialPortFailure,
			SerialPortOutputStreamCloseFailure {
		
		OutputStream out = null;
		try {
			out = serialPort.getOutputStream();//得到输出流
			out.write(order);//写入order
			out.flush();//刷新这个输出流并强制任何缓冲的输出字节被写出
		} catch (IOException e) {
			throw new SendDataToSerialPortFailure();
		} finally {
			try {
				if (out != null) {
					out.close();
					out = null;
				}
			} catch (IOException e) {
				throw new SerialPortOutputStreamCloseFailure();
			}
		}
	}

	/**
	 * 从串口读取数据
	 * 
	 * @param serialPort
	 *            当前已建立连接的SerialPort对象
	 * @return 读取到的数据
	 * @throws ReadDataFromSerialPortFailure
	 *             从串口读取数据时出错
	 * @throws SerialPortInputStreamCloseFailure
	 *             关闭串口对象输入流出错
	 */
	public static byte[] readFromPort(SerialPort serialPort)
			throws ReadDataFromSerialPortFailure,
			SerialPortInputStreamCloseFailure {
		InputStream in = null;
		byte[] bytes = null;
		try {
			in = serialPort.getInputStream();
			// 获取buffer里的数据长度
			int bufflenth = in.available();
			//返回可从此输入流中读取（或跳过）的字节数的估计值,length
			while (bufflenth != 0) {
				// 初始化byte数组为buffer中数据的长度
				bytes = new byte[bufflenth];
				in.read(bytes);
				//从输入流中读取一些字节数，并将它们存储到缓冲区数组bytes中。 实际读取的字节数作为整数返回
				bufflenth = in.available();
			}
		} catch (IOException e) {
			throw new ReadDataFromSerialPortFailure();
		} finally {
			try {
				if (in != null) {
					in.close();
					in = null;
				}
			} catch (IOException e) {
				throw new SerialPortInputStreamCloseFailure();
			}
		}
		return bytes;
	}

	/**
	 * 添加监听器
	 * 
	 * @param port
	 *            串口对象
	 * @param listener
	 *            串口监听器
	 * @throws TooManyListeners
	 *             监听类对象过多
	 */
	public static void addListener(SerialPort port,
			SerialPortEventListener listener) throws TooManyListeners {
		try {
			// 给串口添加监听器
			port.addEventListener(listener);
			// 设置当有数据到达时唤醒监听接收线程
			port.notifyOnDataAvailable(true);
			// 设置当通信中断时唤醒中断线程
			port.notifyOnBreakInterrupt(true);
		} catch (TooManyListenersException e) {
			throw new TooManyListeners();
		}
	}
}
