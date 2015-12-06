import java.io.FileOutputStream;
import java.io.InputStream;  
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;

public class Downloader extends Thread {

    // �����ص����ֽ������raf��  
    private RandomAccessFile raf;  
    private String url;
    
    public Downloader(String url, RandomAccessFile raf) {
    	System.out.println("�����߳����أ�" + url);
    	this.url = url;
        this.raf = raf;  
    }  
    
	public void run() {  
        try {  
        	// ����URL  
	        URL url = new URL(this.url);  
	        // ������  
	        URLConnection con = url.openConnection();  
	        //��������ʱΪ5s  
	        con.setConnectTimeout(5*1000); 
	        //����UA
	        con.setRequestProperty("User-Agent", "Mozilla/4.0 "); 
	        // ������  
	        InputStream is = con.getInputStream();  
	      
	        // 5K�����ݻ���  
	        byte[] bs = new byte[1024 * 5];  
	        // ��ȡ�������ݳ���  
	        int len;  
	        // ������ļ���  
	        // ��ʼ��ȡ  
	        while ((len = is.read(bs)) != -1) {  
	          this.raf.write(bs, 0, len);  
	        }  
	        // ��ϣ��ر���������  
	        is.close();
	        raf.close();
	        System.out.println("������ϣ�" +  this.url);
        } catch (Exception ex) {  
            ex.printStackTrace();  
        }  
        finally {  
            try {  
                if (raf != null) {  
                    raf.close();  
                }  
            } catch (Exception ex) {  
                ex.printStackTrace();  
            }  
        }  
    }  
}
