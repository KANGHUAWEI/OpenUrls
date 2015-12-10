import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;


public class Downloader extends Thread {
	/**
	 * ������Դ���
	 */
	public static final int MAX_TRY_TIMES = 3;
	/**
	 * �߳�ID��Ψһ��ʾ��
	 */
	public int id;
	/**
	 * �߳������Գ��ԵĴ���������ﵽ3�Σ������ˣ�
	 */
	public int tryTimes;
	/**
	 * ��Ҫ���ص�ͼƬ���ӵ�ַ
	 */
	public String imgUrl;
	/**
	 * �����ص����ֽ������raf��  
	 */
    public RandomAccessFile raf; 
	/**
	 * ��������쳣ʱ�Ļص�
	 */
	public IDownloaderCallback callback;
	
	public Downloader(int id, RandomAccessFile raf, String imgUrl, int tryTimes, IDownloaderCallback callback) {
		assert(raf != null && imgUrl != null && callback != null && tryTimes >= 1 && tryTimes <= MAX_TRY_TIMES);
		this.id = id;
		this.raf = raf;
		this.imgUrl = imgUrl;
		this.callback = callback;
		this.tryTimes = tryTimes;
	}

	@Override
	public void run() {
		super.run();	
		System.out.println("�߳�:" + this.id + " ��" + tryTimes + "��ִ��...");
		this.Download(this.imgUrl, this.raf);		
	}
	
	public void Download(String imgUrl, RandomAccessFile raf)
	{
		try {  
        	// ����URL  
	        URL url = new URL(imgUrl);  
	        // ������  
	        URLConnection con = url.openConnection();  
	        //��������ʱΪ10s  
	        con.setConnectTimeout(10*1000); 
	        //����UA
	        con.setRequestProperty("User-Agent", "Mozilla/4.0 "); 
	        // ������  
	        InputStream is = con.getInputStream();  
	      
	        // 1K�����ݻ���  
	        byte[] bs = new byte[1024 * 1];  
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
	        System.out.println("�߳�:" + this.id + " ִ�гɹ�����");
        } catch (Exception e) {
			if(callback != null) {
				callback.onException(this, e);	//�����쳣���������̣߳������TimeOutException��������ӣ�
			}
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
