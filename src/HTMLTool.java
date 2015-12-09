import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.util.NodeList;


public class HTMLTool {
	
	private static final String ENCODE = "gb2312";
	private String prefix;
	
	/**
	 * ����Html����
	 * @param webUrlPrefix ��վǰ׺�����磺http://cl.totu.me/����������ӽ�������ֻ�ܽ�����Σ���htm_data/2/1111/30611.html
	 */
	public HTMLTool(String webUrlPrefix)
	{
		assert(webUrlPrefix != null && webUrlPrefix.startsWith("http") && webUrlPrefix.endsWith("/"));
		this.prefix = webUrlPrefix;
	}
	
	/**
	 * ��ȡHTML���루UA���ô�����ܵ��·���������403�������ش�����;���ߣ�
	 * @param pageURL ���ӵ�ַ
	 * @param encoding �����ʽ����gb2312�ȣ�
	 * @return htmlԴ����
	 */
 	public String getHtmlCodeOfPost(String pageURL) { 
        StringBuilder pageHTML = new StringBuilder(); 
        try { 
            URL url = new URL(pageURL); 
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/4.0 "); 
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), ENCODE));
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
	 * ��CAOLIU���html�����У���ȫ�����ӵ����ƺ����ӵ�ַ������������������+Url�ɶԵķ��ط�ʽ��
	 * @param htmlCode ��������б��html����
	 * @return ����+Url�ɶԵĽ��ֵ�����磺����1��Url1������2��Url2... ����
	 */
	public List<String> parsePostNamesAndUrlsFromHtmlList(String htmlCode)
	{
		List<String> results = new ArrayList<String>();
		try {
			Parser parser = new Parser(htmlCode);
			parser.setEncoding(ENCODE); 
			TagNameFilter filter = new TagNameFilter("h3");  
			NodeList nodeList = parser.parse(filter);
			if(nodeList != null) {
				for (int i = 0; i < nodeList.size(); i++) {
					//e.g. <h3><a href="htm_data/2/1111/30611.html">Re:����ۻ������һ ..</a></h3>
					Node node = nodeList.elementAt(i);				//<h3>
					Tag tag = (Tag)node.getFirstChild();			//<a>
					
					String urlSuffix = tag.getAttribute("href");	//htm_data/2/1111/30611.html
					String postName = node.toPlainTextString();		//Re:����ۻ������һ ..  
					postName = postName.replace("/", "-").replace("\\", "-").replace(":", "-")
							.replace("*", "-").replace("?", "-").replace("\"", "-").replace("<", "-")
							.replace(">", "-").replace("|", "-");	//ȥ��������Windows�ļ������ַ�/\:*?"<>|
					
					if(urlSuffix.startsWith("htm_data")) {			//��ȥread.php?tid=1315��������̳����
						
						results.add(postName);	
						results.add(prefix + urlSuffix);			//http://cl.totu.me/htm_data/2/1111/30611.html
					}
				}
			}
        }
        catch( Exception e ) {     
            e.printStackTrace();
        }
		return results;
	}
	
	/**
	 * ��¥�������������н�����Ԥ��ͼ��ַ
	 * @param htmlCode ���ӵ�ַ
	 * @return List<String> Ԥ��ͼ����
	 */
	public List<String> parseImagesUrlFromPost(String htmlCode)
	{
		List<String> results = new ArrayList<String>();
		try {
			Parser parser = new Parser(htmlCode);
	        parser.setEncoding(ENCODE);
	        //<div class="tpc_content do_not_catch">	����¥��ı�ǩ��������¥����
	        NodeList nodeList = parser.extractAllNodesThatMatch(new NodeFilter() {
	        	//ȡtagName��div�ģ�����class���Եģ���classֵ��Ϊ�գ�����tpc_content do_not_catch�Ľڵ�
	            public boolean accept(Node node) {
	                return ((node instanceof Tag)
	                        && !((Tag)node).isEndTag()
	                        && ((Tag)node).getTagName().equals("DIV")	//��鲻��дСд��div�������޷�ƥ�䵽���壩
	                        && ((Tag)node).getAttribute("class") != null
	                        && ((Tag)node).getAttribute("class").equals("tpc_content do_not_catch"));
	            }
	        });
	        if(nodeList == null || nodeList.size() == 0) {
	        	System.out.println();
	        	return null;
	        }
	        Node master = nodeList.elementAt(0);	//ֻҪ¥��
	        /**
	         * ��¥����Ϣ��ȡ<img src='http://t2.imgchili.net/74202/74202373_1rct00786pl.jpg'
	         * �м������<br>�ڵ㣬��ֻҪû��<a href='..>�ڵ㣬�ͼ���ȡͼ�������жϣ����治�Ǳ���ͼƬ��
	         */
	        Boolean isBegin = false;	//�Ƿ�ʼȡͼ�ˣ���ʼ��������һ��<a href='..>���ж�ȡͼ��
	        NodeList nodes = master.getChildren();
	        for(int i = 0; i < nodes.size(); i++) {
	        	Node node = nodes.elementAt(i);
	        	if(node instanceof Tag) {	//�Թ�TextNode�������޷�cast��Tag���ͣ����쳣��
	        		Tag tag = (Tag)node;
	        		//<img src='http://t2.imgchili.net/74202/74202373_1rct00786pl.jpg'>
	        		if(tag.getTagName().equalsIgnoreCase("IMG") && tag.getAttribute("src") != null) {
		        		isBegin = true;	//ȡ����һ��ͼ������ȡͼ��ʼ
		        		results.add(tag.getAttribute("src"));
		        	}
		        	if(isBegin) {
		        		//��ʼ����ȡͼ��������һ��������Ԫ�أ���ʾ����������ӰƬ��Ԥ��ͼ�ˣ���ֹͣȡͼ
		        		if(tag.getTagName().equalsIgnoreCase("A") && tag.getAttribute("href") != null) {
			        		break;
			        	}
		        	}
	        	}
	        }
        }
        catch( Exception e ) {     
            e.printStackTrace();
        }
		return results;
	}
	
	/**
	 * ��¥�������������н�����BT����ҳ���ַ
	 * @param htmlCode htmlCode ¥����html����
	 * @return BT����ҳ��
	 */
	public String parseBTSeedUrlFromPost(String htmlCode)
	{
		try {
			Parser parser = new Parser(htmlCode);
	        parser.setEncoding(ENCODE);
	        //http://www.rmdown.com/link.php?hash=xxx
	        NodeList nodeList = parser.extractAllNodesThatMatch(new NodeFilter() {
	        	//ȡtagName��div�ģ�����class���Եģ���classֵ��Ϊ�գ�����tpc_content do_not_catch�Ľڵ�
	            public boolean accept(Node node) {
	                return ((node instanceof TextNode)	
	                        && ((TextNode)node).getText().contains("http://www.rmdown.com"));
	            }
	        });
	        if(nodeList != null && nodeList.size() > 0) {
	        	return ((TextNode)nodeList.elementAt(0)).getText();
	        }
        }
        catch( Exception e ) {     
            e.printStackTrace();
        }
		return null;
	}
}
