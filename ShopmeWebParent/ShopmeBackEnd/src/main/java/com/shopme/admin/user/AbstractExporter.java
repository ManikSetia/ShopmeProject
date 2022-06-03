package com.shopme.admin.user;

import com.shopme.common.entity.User;

import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AbstractExporter {

    public void setResponseHeader(HttpServletResponse response, String extension, String contentType){
        DateFormat formatter=new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String timeStamp=formatter.format(new Date());
        String fileName="users_"+timeStamp+extension;

        response.setContentType(contentType);

        //for allowing the browser to download the file
        String headerKey="Content-Disposition";
        String headerValue="attachment; filename="+fileName;
        response.setHeader(headerKey, headerValue);
    }
}
