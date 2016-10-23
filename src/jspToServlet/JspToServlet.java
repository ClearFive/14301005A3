package jspToServlet;


import java.io.File;
import java.io.IOException;
import java.util.Date;

import server.Response;
import server.Constants;



public class JspToServlet {
	static final String EOL = "\n";
	
	public static String getJsp_ServletName(String uri) {
		String jname = uri.substring(uri.lastIndexOf('/') + 1, uri.lastIndexOf('.')) + "_jsp";
		String fname = jname + ".java";
		File fp = new File(Constants.JSP_JAVA_ROOT,fname);
		if(fp.exists()){
			return jname;
		}
		return null;
	}
	
	//jsp to java
	public static String jspTOServlet(String jspFile,String jname,Response response) throws IOException{
		
		 StringBuffer sb = new StringBuffer();
		 String allFile = jspFile;
		 String temp = "";
		 String im = null;
		 allFile = allFile.replace("\r\n", "");
		 allFile = allFile.replace("\n", "");
		 System.out.println(allFile);
		//<% %> <%= %> <%-- --%> <%@ %> <%! %>
		 int p1=0;
		 int p2=0;
		 int p3=0;
		 while(true){
			 p2=allFile.indexOf("<%", p1);
			 if(p2==-1){
				 sb.append(EOL);
				 sb.append("\tout.println(\"");
				 sb.append(allFile.substring(p1, allFile.length()));
				 sb.append("\");");
				 
				 break;
			 }else if (p2>p1){
				 sb.append(EOL);
				 sb.append("\tout.println(\"");
				 sb.append(allFile.substring(p1, p2-1));
				 sb.append("\");");
				
			 }
			 
			 if(allFile.charAt(p2+2)=='-'){
				 p3=allFile.indexOf("%>", p2);
				 sb.append(EOL);
				 sb.append("\tout.println(\"");
				 sb.append(allFile.substring(p2, p3+2));
				 sb.append("\");");
				 p1=p2=p3+2;
				 p3=p1;
			 }else if(allFile.charAt(p2+2)=='='||allFile.charAt(p2+2)=='!'){
				 p3=allFile.indexOf("%>", p2);
				 sb.append(EOL);
				 sb.append("\tout.println(");
				 sb.append(allFile.substring(p2+3, p3));
				 sb.append(");");
				 p1=p2=p3+2;
				 p3=p1;
			 }else if(allFile.charAt(p2+2)=='@'){
				 p3=allFile.indexOf("%>", p2);
				 im = allFile.substring(p2+3, p3);
				 int m = im.indexOf("import");
				 m=im.indexOf("\"",m);
				 int n =im.indexOf("\"",m+1);
				 im = im.substring(m+1, n-1);
				 p3=allFile.indexOf("%>", p2);
				 p1=p2=p3+2;
				 p3=p1;
			 }else {
				 p3=allFile.indexOf("%>", p2);
				 sb.append(EOL);
				 sb.append(allFile.substring(p2+3, p3));
				 p1=p2=p3+2;
				 p3=p1;
			 }	
		 }
		 
		 String imm[] = im.split(",");
		 StringBuffer sb0 = new StringBuffer();
		 sb0.append("package servlet;");
		 sb0.append(EOL);
		 sb0.append("import java.util.*;");
		 sb0.append(EOL);
		 sb0.append("import javax.servlet.*;");
		 sb0.append(EOL);
		 sb0.append("import javax.servlet.http.*;");
		 sb0.append(EOL);
		 sb0.append("import javax.servlet.jsp.*;");
		 sb0.append(EOL);
		 sb0.append("import java.io.IOException;");
		 sb0.append(EOL);
		 sb0.append("import java.io.PrintWriter;");
		 sb0.append(EOL);
		 for(int i=0;i<imm.length;i++){
			 sb0.append("import ");
			 sb0.append(imm[i]+";");
			 sb0.append(EOL);
		 }
		 sb0.append("public class "+jname+" implements Servlet {");
		 sb0.append(EOL);
		 sb0.append(" public void init(ServletConfig config) throws ServletException {");
		 sb0.append(EOL);
		 sb0.append(" System.out.println(\"init\");");
		 sb0.append(EOL);
		 sb0.append(" }");
		 sb0.append(EOL);
		 sb0.append("public void service(ServletRequest request, ServletResponse response)");
		 sb0.append(EOL);
		 sb0.append("throws ServletException, IOException {");
		 sb0.append(EOL);
		 sb0.append("System.out.println(\"from service\");");
		 sb0.append(EOL);
		 sb0.append("PrintWriter out = response.getWriter();");
		 sb0.append(EOL);
		 
		 sb0.append(sb);
	 
		 sb0.append("}");
		 sb0.append(EOL);
		 sb0.append("public void destroy() {");
		 sb0.append(EOL);
		 sb0.append("System.out.println(\"destroy\");");
		 sb0.append(EOL);
		 sb0.append(" }");
		 sb0.append(EOL);
		 sb0.append("public String getServletInfo() {");
		 sb0.append(EOL);
		 sb0.append("  return null;");
		 sb0.append(EOL);
		 sb0.append(" }");
		 sb0.append(EOL);
		 sb0.append("public ServletConfig getServletConfig() {");
		 sb0.append(EOL);
		 sb0.append(" return null;");
		 sb0.append(EOL);
		 sb0.append("}");
		 sb0.append(EOL);
		 sb0.append("}");
		 sb0.append(EOL);
		 
		
		//System.out.println(sb0);
		String ret = JspToServlet.getRemoveSubstr(sb0.toString().trim(), "out.println(\"\");");
		return ret;	
	}
	
	
	private static String getRemoveSubstr(String src, String sub) {
		int last = 0, cur = 0;
		String str = "";
		while(true){
			cur = src.indexOf(sub, last);
			if(cur == -1)
				break;
			//
			str += src.substring(last, cur);
			last = cur + sub.length();
		}
		if(last < src.length())
			str += src.substring(last, src.length());
		
		return str;
	}
	

	//java to class
	public static int  compile(String des, String src) {
		//dynamic complie
		String[] commadline = {"-d",des,src};
		int status;
		//logs
		System.out.println("complie-log:\n" + "des:" + des + "\nsrc: " + src + "\n");
		
		try{
			status = com.sun.tools.javac.Main.compile(commadline);
		}catch (Exception e) {
			// done
			e.printStackTrace();
			return -1;
		}
	     
		return status;
	}
	
}
