package com.unity;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
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
    
    /**
     * Creates and writes the webpage specified by the passed site to the passed response.
     * @param response The response the webpage is being written to.
     * @param site The site from which the content is being pulled.
     * @throws IOException If Jsoup.connect() fails
     */
    private void CreatePage(HttpServletResponse response, String site) throws IOException {
    	Document currpage = Jsoup.connect("https://unity.com/madewith/" + site).get();
    	
    	//Get the title of the project.
    	Elements header = currpage.getElementsByTag("meta");
    	String title = "";
    	for (Element h : header) {
    		if (h.attr("name").equals("title")) {
    			title = h.attr("content");
    			break;
    		}
    	}
    	
    	// Get the content.
        Elements imgs = currpage.getElementsByTag("img");
        Elements ps = currpage.getElementsByTag("p");
        Elements links = currpage.getElementsByTag("a");
        Elements vids = currpage.getElementsByTag("div");

        response.setContentType("text/html");
		PrintWriter printWriter = response.getWriter();
		
		printWriter.println("<h1>" + title + "</h1>");
		
		// Add vids to the response
		boolean hasVids = false;
		printWriter.println("<h2>VIDEOS</h2>");
        for (Element v : vids) { // Vimeo vid id's are stored in vm-video divs under the data-vm field.
        	if (v.attr("class").equals("vm-video")) {
        		hasVids = true;
        		printWriter.println("<iframe src=\"https://player.vimeo.com/video/" + v.attr("data-vm") + "?autoplay=1\" width=\"640\" height=\"360\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>");		
        	}
        }
        if (!hasVids) {
        	printWriter.println("<h3>(this title has no videos!)</h3>");
        }
		
		// Print imgs to the response
		printWriter.println("<h2>IMGS</h2>");
        for (Element i : imgs) {
        	String src = i.attr("src");
        	if (!src.contains("https://unity.com")) src = "https://unity.com/" + src;  // Add unity.com to the source urls if not present
    		printWriter.println("<img src=\"" + src +"\">");		
        }
        
        // Print text to the response
		printWriter.println("<h2>TEXT</h2>");
        for (Element p : ps) {
    		printWriter.println("<p>" + p + "</p>");		
        }
        
        // Print links to the response
 		printWriter.println("<h2>LINKS</h2>");
        for (Element l : links) {
     	   String href = l.attr("href");
     	   if (!href.contains("https://unity.com") && !href.contains("https://unity3d.com")) href = "https://unity.com/" + href;  // Add unity.com to the source urls if not present
 		   printWriter.println("<a href=\"" + href + "\">" + href + "</a><br>");		
        }
    }
    
    /**
     * Stores the projects on the MWU site in the ArrayList<> projects
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
		Cookie[] cookies = request.getCookies(); // Check if client has cookie.  Cookie value = index in projects.
		if (cookies != null) {
			for (Cookie c : cookies) {
				if (c.getName().equals("curr_site") && !c.getValue().contentEquals("unruly-heroes")) {
					curr = c;
					int curr_index = Integer.parseInt(c.getValue());
					if (curr_index == projects.size() - 1) c.setValue(0 + "");
					else c.setValue((curr_index + 1) + "");
				}
			}
		}
		if (curr == null) {
			curr = new Cookie("curr_site", "0");
		}
		response.addCookie(curr);
		CreatePage(response, projects.get(Integer.parseInt(curr.getValue())));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
