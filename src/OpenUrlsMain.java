import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.sound.sampled.*;

public class OpenUrlsMain { 
	//CAOLIU����
	private static String hostUrl = "http://cl.totu.me/";
       
    public static void main(String args[]){
    	if (args.length > 0) {
			String tmp = args[0];
			if(tmp.equalsIgnoreCase("-h") || tmp.equalsIgnoreCase("-help")) {
				System.out.println("���÷�����java OpenUrlsMain http://cl.totu.me\n�����޸Ĳ���ĵ�ַǰ׺�������Ͳ��þ���������");
				return;
			}
			else if(!tmp.startsWith("http")) {
				System.out.println("���棺ǰ׺����Ӧ����http��ͷ�ĸ�ʽ����:http://cl.totu.me/");
				return;
			}
			hostUrl = tmp;
		}
    	
    	File root = new File("D:\\");
    	if(root.isDirectory()) {
    		for(File f : root.listFiles()) {
    			if(f.getName().endsWith(".html") || f.getName().endsWith(".htm")) {
    				System.out.println("�����ļ���" + f.getName());
    				//����001.html�ļ���Ӧ���ļ���D:\001
    				String dirPath = f.getName().replace(".html", "").replace(".htm", "");
    				File dir = new File(String.format("D:\\%s", dirPath));
    				if(!dir.exists()) {
    					dir.mkdirs();
    				}
    				//��ȡ001.html������
    				StringBuilder sb = new StringBuilder();
    				File file = new File(String.format("D:\\%s", f.getName()));
    		        BufferedReader reader = null;
    		        try {
    		            reader = new BufferedReader(new FileReader(file));
    		            String tempString = null;
    		            while ((tempString = reader.readLine()) != null) {
    		            	sb.append(tempString+"\n");
    		            }
    		            reader.close();
    		        } catch(Exception e) {
    		        	e.printStackTrace();
    		        }
    				
    		        //����001.html�ļ�
    		        HTMLTool htmlTool = new HTMLTool(hostUrl);
    		        //HtmlParser֧��ֱ�Ӵ���D:\\001.htm���������ܽ����������ģ����Դ���html����
    				List<String> posts = htmlTool.parsePostNamesAndUrlsFromHtmlList(sb.toString());
//    				for (String string : strings) {
//						System.out.println(string);
//					}
    				assert(posts.size() / 2 == 0);	//���ݱ���ɶԳ���
    				
    				//�����̳߳�
    				ExecutorService es = Executors.newCachedThreadPool();
    				final ThreadPoolExecutor executor = (ThreadPoolExecutor)es;
    				for(int i = 1; i < posts.size(); i+=2) {
    					//���������ļ���
    					String folderName = posts.get(i - 1);
    					File subDir = new File("D:\\" + dirPath + "\\" + folderName);
    			    	if(!subDir.exists()) {
    			    		subDir.mkdirs();
    			    	}
    			    	//������ǰ���ӵ�����ͼƬ
    			    	String postUrl = posts.get(i);
    			    	String code = htmlTool.getHtmlCodeOfPost(postUrl);
    			    	List<String> images = htmlTool.parseImagesUrlFromPost(code);
    			    	//�����ص����󣨴���ʧ�ܣ�
    			    	IDownloaderCallback callback = new IDownloaderCallback() {
							
							@Override
							public void onException(Downloader d, Exception e) {
								System.out.println("�̳߳���" + d.id + " " + e);
								if(d.tryTimes < Downloader.MAX_TRY_TIMES) {
									Downloader dd = new Downloader(d.id, d.raf, d.imgUrl, d.tryTimes + 1, d.callback);
									executor.execute(dd);
									System.out.println("�߳����ԣ�" + d.id + "��" + d.tryTimes + "��");
								} else {
									System.out.println("�̣߳�" + d.id + " �ﵽ����������ƣ��޷����أ�" + d.imgUrl);
								}
							}
						};
    			    	for(int j = 0; j < images.size(); j++) {
    			    		//ѭ�����������߳�
    			    		String imgUrl = images.get(i);
    			    		int id = (i - 1) * 100 + j + 1;
    			    		RandomAccessFile raf;
							try {
								raf = new RandomAccessFile(subDir.getPath()+"\\"+ String.format("%3d.jpg",j + 1), "rw");
								Downloader d = new Downloader(id, raf, imgUrl, 1, callback);
	    			    		executor.execute(d);
							} catch (FileNotFoundException e1) {
								e1.printStackTrace();
							}
    			    	}
    			    	//дBT�����ļ�
    			    	File btTxt = new File(subDir.getAbsolutePath() + "\\" + folderName + ".txt");
    					try {
							BufferedWriter bw = new BufferedWriter(new FileWriter(btTxt));
							String bt = htmlTool.parseBTSeedUrlFromPost(code);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
    				}
    				 System.out.println("�̳߳����߳���Ŀ��"+executor.getPoolSize()+"�������еȴ�ִ�е�������Ŀ��"+
    			             executor.getQueue().size()+"����ִ������������Ŀ��"+executor.getCompletedTaskCount());
//    				break;
    			}
    		}
    	}
    	
    	//���������ʾ��
    	try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(new File("D:\\complete.wav"));
			AudioFormat aif = ais.getFormat();
			SourceDataLine sdl = null;
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, aif);
            sdl = (SourceDataLine) AudioSystem.getLine(info);
            sdl.open(aif);
            sdl.start();
            int nByte = 0;
            byte[] buffer = new byte[1024 * 10];
            while (nByte != -1) {
                nByte = ais.read(buffer, 0, 1024 * 10);
                if (nByte >= 0) {
                    sdl.write(buffer, 0, nByte);
                }
            }
            sdl.stop();
		} catch (Exception e) {
			e.printStackTrace();
		} 
    	System.out.println("ִ�����");
    } 
} 