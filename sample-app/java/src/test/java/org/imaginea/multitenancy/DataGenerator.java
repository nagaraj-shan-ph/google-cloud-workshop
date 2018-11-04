package org.imaginea.workshop;

import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.fluttercode.datafactory.impl.DataFactory;

public class DataGenerator {

  public static void main(String[] args) throws Exception {

    int numberOfRows = 1000000;
    // Create a collection of all available countries
    String[] locales = Locale.getISOCountries();

    List<String> countries = new ArrayList<>();
    for (String countryCode : locales) {
      Locale obj = new Locale("", countryCode);
      countries.add(obj.getDisplayCountry());
    }

    DataFactory df = new DataFactory();

    Map<String, String> data = new LinkedHashMap<>();

    // create FileWriter object with file as parameter
    File file = new File("/home/nagarajans/listdata/list_data_" + numberOfRows + ".csv");
    FileWriter fileWriter = new FileWriter(file);

    // create CSVWriter object filewriter object as parameter
    CSVWriter writer = new CSVWriter(fileWriter);

    boolean isHeaderAdded = false;
    // adding header to csv
    HashSet<String> emailIdUnique = new HashSet<>();

    for (int j = 0; j <= numberOfRows; j++) {
      data.clear();
      String last1 = df.getLastName();
      String first1 = df.getFirstName();
      String uuid1 = UUID.randomUUID().toString();
      String emailId = first1.concat(last1).concat("_").concat(uuid1).concat("@gmail.com");
      if (!emailIdUnique.contains(emailId)) {
        emailIdUnique.add(emailId);
        data.put("Address 1", df.getAddress());
        data.put("Address 2", df.getAddressLine2());
        data.put("City", df.getCity());
        data.put("Company", df.getBusinessName());
        data.put("Email", emailId);
        data.put("Country", df.getItem(countries));
        data.put("First Name", first1);
        data.put("Last Name", last1);
        data.put("Middle Name", df.getLastName());
        data.put("Mobile", String.valueOf(df.getNumberText(10)));
        data.put("State/Province", df.getStreetName());
        data.put("Postal Code", String.valueOf(df.getNumberBetween(10000, 99999)));
        if(!isHeaderAdded){
          String[] header = data.keySet().toArray(new String[data.keySet().size()]);
          writer.writeNext(header);
          isHeaderAdded = true;
        }
        // add data to csv
        writer.writeNext(data.values().toArray(new String[data.keySet().size()]));

      }
    }
    // closing writer connection
    writer.close();
  }
}
