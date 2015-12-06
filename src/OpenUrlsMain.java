import java.io.BufferedReader; 
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader; 
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection; 
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL; 
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.awt.Toolkit;

public class OpenUrlsMain { 
	//��������
	private static String hostUrl = "http://cl.totu.me/";
	//XIAAV��̳
//	private static String hostUrl = "http://xav3.info/";
//	private static String requestUrl = "http://xav3.info/forum.php";
	
	private static int BREAK_COUNT = 1001;
    
	//��ȡHTML���루UA���ô�����ܵ��·���������403��
	public static String getHTML(String pageURL, String encoding) { 
        StringBuilder pageHTML = new StringBuilder(); 
        try { 
            URL url = new URL(pageURL); 
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/4.0 "); 
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), encoding));
            String line = null; 
            while ((line = br.readLine()) != null) { 
                pageHTML.append(line); 
                pageHTML.append("\r\n"); 
            } 
            connection.disconnect(); 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } 
        return pageHTML.toString(); 
    } 
	
	/**
	 * ��������ͼƬ
	 * @param urlString
	 * @param filename
	 * @param savePath
	 * @throws Exception
	 */
	 public static void download(String urlString, String filename,File sf) throws Exception {  
	        // ����URL  
	        URL url = new URL(urlString);  
	        // ������  
	        URLConnection con = url.openConnection();  
	        //��������ʱΪ5s  
	        con.setConnectTimeout(15*1000); 
	        //����UA
	        con.setRequestProperty("User-Agent", "Mozilla/4.0 "); 
	        // ������  
	        InputStream is = con.getInputStream();  
	      
	        // 1K�����ݻ���  
	        byte[] bs = new byte[1024];  
	        // ��ȡ�������ݳ���  
	        int len;  
	        // ������ļ���  
//	       File sf=new File(savePath);  
//	       if(!sf.exists()){  
//	           sf.mkdirs();  
//	       }  
	       OutputStream os = new FileOutputStream(sf.getPath()+"\\"+filename);  
	        // ��ʼ��ȡ  
	        while ((len = is.read(bs)) != -1) {  
	          os.write(bs, 0, len);  
	        }  
	        // ��ϣ��ر���������  
	        os.close();  
	        is.close();  
	    }   
	
    /**
     * ����Ϊ��λ��ȡ�ļ��������ڶ������еĸ�ʽ���ļ�
     */
    public static void readFileByLines(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
//            System.out.println("����Ϊ��λ��ȡ�ļ����ݣ�һ�ζ�һ���У�");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // һ�ζ���һ�У�ֱ������nullΪ�ļ�����
            while ((tempString = reader.readLine()) != null) {
            	if(line > BREAK_COUNT)
            	{
            		break;
            	}
                // ��ʾ�к�
//                System.out.println("line " + line + ": " + tempString);
//                line++;
//            	System.out.println(tempString);
            	
            	//�������������߼�
            	if(tempString.contains("<h3><a href=") && tempString.contains("</a></h3>"))
            	{
            		String DirName = tempString.substring(tempString.indexOf("\">") + 2, tempString.indexOf("</a></h3>")).replace("/","-").replace("?", "").replace("&nbsp;", "");
                    System.out.println("line " + line + ": " + DirName);
                    line++;
            		int subUrlBegin = tempString.indexOf("htm_data");
            		int subUrlEnd = tempString.indexOf(".html");
            		if(subUrlEnd == -1)	//û��.html�Ĳ���
            		{
            			continue;
            		}
            		String url = hostUrl + tempString.substring(subUrlBegin, subUrlEnd + 5);	//5: .html
            		try{
            			//v1.0��ֱ����Ĭ�����������ҳ���ٶ�����ռ��Դ�࣬���������Բ��Դɸѡ�ȽϷ���
//            			java.awt.Desktop.getDesktop().browse(new URI(url));  
//            			Thread.sleep(1200);
            			
            			
            			
            			OpenUrlsMain.parseCaoLiuPostInfo(fileName, DirName, url);
            		}
            		catch(Exception ex)
            		{
            			
            		}
            	}
            	//XIAAV�����߼�
//            	if(tempString.contains("<a href=") && tempString.contains("onclick=\"atarget(this)\""))
//            	{
//                    System.out.println("line " + line + ": " + tempString);
//                    line++;
//            		int subUrlBegin = tempString.indexOf("thread");
//            		int subUrlEnd = tempString.indexOf(".html");
//            		if(subUrlEnd == -1)	//û��.html�Ĳ���
//            		{
//            			continue;
//            		}
//            		String url = hostUrl + tempString.substring(subUrlBegin, subUrlEnd + 5);	//5: .html
//            		try{
//            			java.awt.Desktop.getDesktop().browse(new URI(url));  
//            			Thread.sleep(2000);
//            		}
//            		catch(Exception ex)
//            		{
//            			
//            		}
//            	}
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        Toolkit kit = Toolkit.getDefaultToolkit();
        kit.beep();
    }
    
    /**
	 * ����������˵����
	 * 
	 * 1��HTML�����У���һ�γ��ֵ�<div class=\"tpc_content do_not_catch>...��/div>��¥��������ֻȡ�����������
	 * 2����¥����HTML�����У���һ��<img src=' ... ��>&nbsp;�ǵ�һ��Ԥ��ͼ��������&nbsp����>�����װѿո�����
	 * &nbsp����׼ȷ����ͨ��������Ԥ��ͼ��HTML�����ж��������ġ����&nbsp;���������һ����ǩ����<img src���ұ�
	 * �ģ�˵����¥����������Դ��棬��֮���ͼƬ�Ͳ���Ҫ��
	 * 3��http://www.rmdown.com/link.php�������ļ��������ַ�����ҳ������Ҫ��������д򿪵ģ�ȷ��������ʱ���ֶ���������
	 */
    public static void parseCaoLiuPostInfo(String folderName, String subDirName, String url)
    {
    	//http://cl.totu.me/htm_data/15/1512/1744793.html 3��
    	//http://cl.totu.me/htm_data/15/1512/1744977.html 1��
    	
    	//���������ļ���
    	File dir = new File(folderName.replace(".html", ""));
    	if(!dir.exists()) {
    		dir.mkdir();
    	}
    	
    	File subDir = new File(dir.getAbsolutePath() + "\\" + subDirName);
    	if(!subDir.exists()) {
    		subDir.mkdir();
    	}
    	
    	//�������ӵ�ַ���õ�html�����ַ���
    	String html = OpenUrlsMain.getHTML(url, "GBK");	
    	
    	//��ȡ¥����Ϣ
    	String louzhuTagStart = "<div class=\"tpc_content do_not_catch";	//ֻ�е�һ����¥����������������˵Ļ���
    	String louzhuTagEnd = "/div";	//ֻ��louzhuTagStart����ĵ�һ��/div��¥����Ϣ����tag����������
    	
    	String yulantuTagStart = "<img src='";	//Ԥ��ͼ��ǩ��ʼ
    	String yulantuTagEnd = ">&nbsp;";		//Ԥ��ͼ��ǩ������ע�⣬��һ��������yulantuTagStart��ͷ����������ͼ�Ͳ��Ǳ������ݣ��Ͳ�Ҫ��
    	
    	int louzhuStartIdx = html.indexOf(louzhuTagStart);
    	int louzhuEndIdx = html.indexOf(louzhuTagEnd, louzhuStartIdx) - 1;
    	String info = html.substring(louzhuStartIdx, louzhuEndIdx);	//�õ�¥�����ݵ�HTML����
//    	System.out.println(info + "\n");
    	
    	//��¥����Ϣ�У���������Ԥ��ͼ
    	int imageStartIdx = info.indexOf(yulantuTagStart);	//<img src='
    	int imageEndIdx = -1; 
    	int imageWhiteSpacePlaceHolderIdx = -1;	//&nbsp;���ֵ�λ��
    	int idx = 0;
    	
    	int cpuNums = Runtime.getRuntime().availableProcessors();  //��ȡ��ǰϵͳ��CPU ��Ŀ
    	ExecutorService executorService =Executors.newFixedThreadPool(cpuNums * 5);	//4��CPU�Ļ���ÿ���Ŀ�5���̣߳����ԣ�
    	
    	StringBuilder sb = new StringBuilder();
    	
    	while(imageStartIdx != -1) {
    		imageEndIdx = info.indexOf("g'",imageStartIdx) + 1;	//http://... .jpg(png?jpeg?����ν��ֻҪ�����g�Ϳ��ԣ�
    		if(imageEndIdx > 0) {
    			String imgUrl = info.substring(imageStartIdx + yulantuTagStart.length(), imageEndIdx);	//Ԥ��ͼ��ַ
    			//TODO:�������Ԥ��ͼ����
    			try {
//					OpenUrlsMain.download(imgUrl, subDirName + String.format("%3d.jpg",idx), subDir);
    				idx++;
    				executorService.execute(new Downloader(imgUrl, new RandomAccessFile(subDir.getPath()+"\\"+ subDirName + String.format("%3d.jpg",idx), "rw")));
				} catch (Exception e) {
					e.printStackTrace();
				}
    			if(imgUrl.length() < 100) {	//TODO��ƥ��������ʱ���е����⣬��ȡ��һЩ����վ�������ٷ�ֱӪ�������Ž�ɳ���ֳ�����1258.com�����������ݣ���ʱ��ȥ������imgUrl
//    				System.out.println(imgUrl);
    				sb.append(imgUrl + "\r\n");
    			}
		    	imageWhiteSpacePlaceHolderIdx = info.indexOf(yulantuTagEnd, imageEndIdx);
		    	imageStartIdx = info.indexOf(yulantuTagStart, imageEndIdx);

		    	/**
		    	 * ˵����>&nbsp;֮�����һ����ǩ�������<img src='��index��Զ����15����ú�Զ�����ԾͲ�Ҫ�ˡ�
		    	 * ֮���Բ�ѡ2���������ŵģ�����Ϊ��Щ¥���Ű�����</br>�����Ļ��з�
		    	 */
		    	if(Math.abs(imageStartIdx - (imageWhiteSpacePlaceHolderIdx + yulantuTagEnd.length())) > 15) {	
		    		break;
		    	}
    		} else {
    			break;	//û�ҵ�.jpg�������ж�ѭ��
    		}
    	}
    	
    	//��BT�������ص�ַ
    	int btSeedStartIdx = info.indexOf("http://www.rmdown.com");	//����һ��
    	int btSeedEndIdx = info.indexOf("</a>", btSeedStartIdx);
    	String btUrl = info.substring(btSeedStartIdx, btSeedEndIdx);
    	System.out.println();
    	System.out.println(btUrl);
    	System.out.println("\n\n\n");
    	try {
			File btTxt = new File(subDir.getAbsolutePath() + "\\" + subDirName + ".txt");
			BufferedWriter bw = new BufferedWriter(new FileWriter(btTxt));
			bw.write(sb.toString());
			bw.write(btUrl);
			bw.write("\r\n");
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static void main(String args[]){
    	if (args.length > 0) {
			String tmp = args[0];
			if(tmp.equalsIgnoreCase("-h")) {
				System.out.println("���÷�����java OpenUrlsMain http://cl.totu.me\n�����޸Ĳ���ĵ�ַǰ׺�������Ͳ��þ���������");
				return;
			}
			else if(!tmp.startsWith("http")) {
				System.out.println("���棺ǰ׺����Ӧ����http��ͷ�ĸ�ʽ����:http://cl.totu.me/");
				return;
			}
			hostUrl = tmp;
		}
    	
    	File file = new File("D:\\");
    	if(file.isDirectory()) {
    		for(File f : file.listFiles()) {
    			if(f.getName().endsWith(".html") || f.getName().endsWith(".htm")) {
    				System.out.println(f.getName());
    				OpenUrlsMain.readFileByLines(String.format("D:\\%s", f.getName()));
//    				f.delete();
    				break;
    			}
    		}
    	}
    	System.out.println("ִ�����");
    } 
} 