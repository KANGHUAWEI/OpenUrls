
/**
 * ����Downloader�߳�����ֿɲ�����쳣ʱ�����ж����̵߳Ļص�����
 */
public interface IDownloaderCallback {
	/**
	 * Downloader�����쳣ʱ����catch���н������׻����߳��Ա������������
	 * @param d	�����쳣��Downloader����
	 * @param e	�������쳣����
	 */
	public void onException(Downloader d, Exception e);
}
