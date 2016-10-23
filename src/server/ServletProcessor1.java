package server;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class ServletProcessor1 {

    public void process(Request request, Response response) {

        String uri = request.getUri();
        String servletName = uri.substring(uri.lastIndexOf("/") + 1);
        
        //������������ڴ�ָ��JAR�ļ���Ŀ¼������
        URLClassLoader loader = null;
        try {
            URLStreamHandler streamHandler = null;
            //�����������
            loader = new URLClassLoader(new URL[]{new URL(null, "file:" + Constants.JSP_CLASS_ROOT, streamHandler)});
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        
        Class<?> myClass = null;
        try {
            //���ض�Ӧ��servlet��
            myClass = loader.loadClass("servlet." + servletName);
        } catch (ClassNotFoundException e) {
            System.out.println(e.toString());
        }

        Servlet servlet = null;

        try {
            //���servletʵ��
            servlet = (Servlet) myClass.newInstance();
            //ִ��ervlet��service����
            servlet.service((ServletRequest) request,(ServletResponse) response);
        } catch (Exception e) {
            System.out.println(e.toString());
        } catch (Throwable e) {
            System.out.println(e.toString());
        }

    }
}