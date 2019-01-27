package com.unity;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
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
		PrintWriter printWriter = response.getWriter();
		printWriter.println(projects.toString());		
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
