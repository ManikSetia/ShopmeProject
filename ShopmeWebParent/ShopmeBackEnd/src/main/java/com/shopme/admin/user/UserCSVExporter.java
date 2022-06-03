package com.shopme.admin.user;

import com.shopme.common.entity.User;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UserCSVExporter {

    public void export(List<User> listUsers, HttpServletResponse response) throws IOException {
        DateFormat formatter=new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String timeStamp=formatter.format(new Date());
        String fileName="users_"+timeStamp+".csv";

        response.setContentType("text/csv");

        //for allowing the browser to download the file
        String headerKey="Content-Disposition";
        String headerValue="attachment; filename="+fileName;
        response.setHeader(headerKey, headerValue);

        //Now we've to use added dependency (super-csv) to write the csv file
        ICsvBeanWriter csvWriter=new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

        //Things to write in the csv file
        String[] csvHeader={"User ID", "E-mail", "First Name", "Last Name", "Roles", "Enabled"};
        String[] fieldMapping={"id", "email", "firstName", "lastName", "roles", "enabled"};

        csvWriter.writeHeader(csvHeader);
        for(User user: listUsers){
            csvWriter.write(user, fieldMapping);
        }

        csvWriter.close();
    }
}
