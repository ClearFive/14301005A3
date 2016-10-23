package jspToServlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

import server.Request;
import server.Response;
import server.ServletProcessor1;
import server.Constants;



public class JspProcessor {
	public void process(Request request, Response response) {
		String jname = JspToServlet.getJsp_ServletName(request.getUri());
		ServletProcessor1 processor = new ServletProcessor1();
		//
		if(jname==null){
			File fp = new File(Constants.JSP_ROOT, request.getUri());
			//logs
			System.out.println("logs jsp file path:" + fp.getAbsolutePath() + "\n");
			//
			if(fp.exists()){
				BufferedReader bufferedReader = null;
				StringBuffer sb = new StringBuffer();
				String jsp_content = null;
				jname = request.getUri().substring(
						request.getUri().lastIndexOf('/') + 1, 
						request.getUri().lastIndexOf('.')) + "_jsp";
				try{			
					bufferedReader = new BufferedReader(new FileReader(fp));
					String str;
					while((str = bufferedReader.readLine()) != null){
						sb.append(str + "\n");
					}
					jsp_content = sb.toString();
					//
					bufferedReader.close();
				}catch (Exception e) {
					// DONE
					e.printStackTrace();
				}
				//
				fp = new File(Constants.JSP_JAVA_ROOT, jname + ".java");
				//create servlet java file for the jsp
				try{
					fp.createNewFile();
					FileOutputStream fos = new FileOutputStream(fp);
					fos.write(JspToServlet.jspTOServlet(jsp_content, jname, response).getBytes());
					fos.close();
				}catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					return;
				}
				
				//compile
				int status = JspToServlet.compile(Constants.JSP_CLASS_ROOT, 
						Constants.JSP_JAVA_ROOT + File.separator + jname + ".java");
				System.out.println(status);
				if(status != 0){
					//logs
					System.out.println("log : " + jname + ".java compiled faile!\n");
				}else{
					//handle servlet
					request.setURI("/" + jname);
					//LOGS
					System.out.println("log reset uri: " + request.getUri() + "\n");
					
					processor.process(request, response);
				}
			}
			else{
				//jsp file not exits
				try{
					response.sendStaticResource();
				}catch (Exception e) {
					//logs
					e.printStackTrace();
				}
			}
			
		}else{
			//logs
			System.out.println("logs: \njsp name: " + jname + "\n");
			
			request.setURI("/" + jname);
			processor.process(request, response);
		}
	}
	
}

