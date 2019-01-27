package com.unity;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
/**
 * Servlet implementation class MadeWithUnity
 */
@WebServlet("/MadeWithUnity")
public class MadeWithUnity extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private List<String> projects;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MadeWithUnity() throws IOException {
        super();
        
        // Get arraylist of URLs for MWU projs
        GetURLs();
    }
    
    private void CreatePage(HttpServletResponse response, String site) throws IOException {
    	Document currpage = Jsoup.connect("https://unity.com/madewith/" + site).get();
    	Elements header = currpage.getElementsByTag("meta");
    	String title = "";
    	for (Element h : header) {
    		if (h.attr("name").equals("title")) {
    			title = h.attr("content");
    			break;
    		}
    	}
        Elements imgs = currpage.getElementsByTag("img");
        Elements ps = currpage.getElementsByTag("p");
        Elements links = currpage.getElementsByTag("a");
        Elements vids = currpage.getElementsByTag("div");
        response.setContentType("text/html");
		PrintWriter printWriter = response.getWriter();
		
		printWriter.println("<h1>" + title + "</h1>");
		
		// Print vids
		/*printWriter.println("<h2>VIDEOS</h2>");
        for (Element v : vids) {
        	if (v.attr("class").contains("section-trailer embed"))
    		printWriter.println(v.attr("iframe"));		
        }*/
		
		// Print imgs
		printWriter.println("<h2>IMGS</h2>");
        for (Element i : imgs) {
        	String src = i.attr("src");
        	if (!src.contains("https://unity.com")) src = "https://unity.com/" + src;
    		printWriter.println("<img src=\"" + src +"\">");		
        }
        
        // Print text
		printWriter.println("<h2>TEXT</h2>");
        for (Element p : ps) {
    		printWriter.println("<p>" + p + "</p>");		
        }
        
        // Print text
 		printWriter.println("<h2>LINKS</h2>");
        for (Element l : links) {
     	   String href = l.attr("href");
     	   if (!href.contains("https://unity.com") && !href.contains("https://unity3d.com")) href = "https://unity.com/" + href;
 		   printWriter.println("<a href=\"" + href + "\">" + href + "</a><br>");		
        }
    }
    
    /**
     * Stores the projects on the MWU site in projects
     * @throws IOException if connect doesn't work.
     */
    private void GetURLs() throws IOException {
    	projects = new ArrayList<String>();
        Document mwu_homepage = Jsoup.connect("https://unity.com/madewith/").get();
        Elements links = mwu_homepage.getElementsByTag("a");
        for (Element e : links) {
        	String url = e.attr("href");
        	if (url.matches("/madewith/.*"))
        		projects.add(e.attr("href").substring(10));
        }
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		Cookie curr = null;
		Cookie[] cookies = request.getCookies(); // Check if client has cookie.
		for (Cookie c : cookies) {
			if (c.getName().equals("curr_site")) {
				curr = c;
				int curr_index = Integer.parseInt(c.getValue());
				if (curr_index == projects.size() - 1) c.setValue(0 + "");
				else c.setValue((curr_index + 1) + "");
			}
		}
		
		if (curr == null) {
			curr = new Cookie("curr_site", "0");
		}
		response.addCookie(curr);
		CreatePage(response, projects.get(Integer.parseInt(curr.getValue())));
		PrintWriter printWriter = response.getWriter();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
