package projekat.pmaiu.androidprojekat;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import model.Attachment;

public class DownloadAttachment extends AsyncTask {

    private ProgressDialog progressDialog;
    private String fileName;
    private String folder;
    private boolean isDownloaded;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//        this.progressDialog = new ProgressDialog();
//        this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        this.progressDialog.setCancelable(false);
//        this.progressDialog.show();
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }

    @Override
    protected void onProgressUpdate(Object[] values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected String doInBackground(Object[] objects) {
        //ovo ne radi, puca kod bufferedreadera
        String dataString = "";
        Log.e("test", "file "+objects[0]);
        File file=new File(objects[0].toString());
        // i have kept text.txt in the sd-card

        // Read text from file
        StringBuilder text = new StringBuilder();

        try {
            Log.e("test", "u try ");
            BufferedReader br = new BufferedReader(new FileReader(file));
            Log.e("test", "u try1 ");
            String line;
            while ((line = br.readLine()) != null) {
                Log.e("test", "line "+line);
                text.append(line);
                text.append('\n');
            }
        } catch (Exception e){
            e.printStackTrace();
            // You'll need to add proper error handling here
        }
        // Set the text
        dataString = text.toString();
        Log.e("test", "data string "+dataString);

        return dataString ;
//        int count;
//        try {
//            URL url = new URL(f_url[0]);
//            URLConnection connection = url.openConnection();
//            connection.connect();
//            // getting file length
//            int lengthOfFile = connection.getContentLength();
//
//
//            // input stream to read file - with 8k buffer
//            InputStream input = new BufferedInputStream(url.openStream(), 8192);
//
//            String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
//
//            //Extract file name from URL
//            fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1, f_url[0].length());
//
//            //Append timestamp to file name
//            fileName = timestamp + "_" + fileName;
//
//            //External directory path to save file
//            folder = Environment.getExternalStorageDirectory() + File.separator + "androiddeft/";
//
//            //Create androiddeft folder if it does not exist
//            File directory = new File(folder);
//
//            if (!directory.exists()) {
//                directory.mkdirs();
//            }
//
//            // Output stream to write file
//            OutputStream output = new FileOutputStream(folder + fileName);
//
//            byte data[] = new byte[1024];
//
//            long total = 0;
//
//            while ((count = input.read(data)) != -1) {
//                total += count;
//                // publishing the progress....
//                // After this onProgressUpdate will be called
//                publishProgress("" + (int) ((total * 100) / lengthOfFile));
//                Log.d(TAG, "Progress: " + (int) ((total * 100) / lengthOfFile));
//
//                // writing data to file
//                output.write(data, 0, count);
//            }
//
//            // flushing output
//            output.flush();
//
//            // closing streams
//            output.close();
//            input.close();
//            return "Downloaded at: " + folder + fileName;
//
//        } catch (Exception e) {
//            Log.e("Error: ", e.getMessage());
//        }
//
//        return "Something went wrong";
    }
}
