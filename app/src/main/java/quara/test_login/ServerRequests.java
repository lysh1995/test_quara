package quara.test_login;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServerRequests {

    //interface for server callback
    interface onDatabaseReturnCallBack {
        void DatabaseReturnExecute(String line);
    }



    ProgressDialog progressDialog;

    public static final int CONNECTION_TIMEOUT = 1000*15;
    public static final String SERVER_ADDRESS = "http://quara2016.web.engr.illinois.edu/";

    public ServerRequests(Context context)
    {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please wait...");
    }

    public String getEncodedData(Map<String,String> data) {
        StringBuilder sb = new StringBuilder();
        for(String key : data.keySet()) {
            String value = null;
            try {
                value = URLEncoder.encode(data.get(key), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if(sb.length()>0)
                sb.append("&");

            sb.append(key + "=" + value);
        }
        return sb.toString();
    }

    HttpURLConnection setupServer(String filename, String encodedStr) throws Exception {
        URL url = new URL(SERVER_ADDRESS + filename);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);

        OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
        writer.write(encodedStr);
        writer.flush();
        return con;
    }

    private void sendDataToDatabase(String encodedStr, String filename, onDatabaseReturnCallBack callback) {
        BufferedReader reader = null;
        try {
            HttpURLConnection con = setupServer(filename, encodedStr);
            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String line;
            while((line = reader.readLine()) != null) {
                sb.append(line);
            }
            line = sb.toString();

            Log.i("custom_check", "The values received in the store part are as follows:");
            Log.i("custom_check",line);

            if (!line.equals("null"))
            {
                //database doesn't return any data, so don't do anything

                //TODO: implement callback
                if (callback != null) {
                    callback.DatabaseReturnExecute(line);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(reader != null) {
                try {
                    reader.close();     //Closing the
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //return returnValue;
    }

    public void storeUserDataInBackground(User user, GetUserCallBack callBack)
    {
        progressDialog.show();
        new StoreUserDateAsyncTask(user, callBack).execute();
    }

    public void fetchUserDataInBackground(User user, GetUserCallBack callBack)
    {
        progressDialog.show();
        new fetchUserDateAsyncTask(user, callBack).execute();
    }

    public void checkUserDataInBackground(User user, GetUserCallBack callBack)
    {
        progressDialog.show();
        new checkUserDateAsyncTask(user, callBack).execute();
    }

    public void createQueue(Queue queue, GetActualQueueCallBack callback){
        progressDialog.show();
        new CreateQueueAsyncTask(queue, callback).execute();
    }

    public void getAllCourseInBackground(User user, GetCourseCallBack callBack)
    {
        progressDialog.show();
        new getAllCourseAsyncTask(user, callBack).execute();
    }

    public void getCourseDescriptionInBackground(Course course, GetDescriptionCallBack callBack)
    {
        progressDialog.show();
        new getAllCourseDescriptionAsyncTask(course, callBack).execute();
    }

    public void getQueueInBackground(Question queue, GetQueueCallBack callBack)
    {
        progressDialog.show();
        new getAllQueueAsyncTask(queue, callBack).execute();
    }

    public void insertQueueInBackground(Question queue, GetQueueCallBack callBack)
    {
        progressDialog.show();
        new InsertQueueAsyncTask(queue, callBack).execute();
    }

    public void insertGradeInBackground(Grade grade, GetGradeCallBack callBack)
    {
        progressDialog.show();
        new InsertGradeAsyncTask(grade, callBack).execute();
    }

    public void getGradeInBackground(User user, GetGradeCallBack callBack)
    {
        progressDialog.show();
        new GetGradeAsyncTask(user, callBack).execute();
    }

    public void deleteQueueInBackground(Question queue, GetQueueCallBack callBack)
    {
        progressDialog.show();
        new DeleteQueueAsyncTask(queue, callBack).execute();
    }

    public void clearQueue(Question queue, GetQueueCallBack callBack){
        progressDialog.show();
        new ClearQueueAsyncTask(queue, callBack).execute();

    }
    public void editQueueInBackground(Question queue, GetQueueCallBack callBack)
    {
        progressDialog.show();
        new EditQueueAsyncTask(queue, callBack).execute();
    }

    public void answerQuestion(Question queue, GetQueueCallBack callBack)
    {
        progressDialog.show();
        new answerQuestionAsyncTask(queue, callBack).execute();
    }

    public void CheckAuthorisationInBackground(TA ta, CheckAuthorisationCallBack callBack)
    {
        progressDialog.show();
        new CheckAuthorisationAsyncTask(ta, callBack).execute();
    }

    public void setOnDutyInBackground(TA ta, UpdateDutyCallBack callBack)
    {
        progressDialog.show();
        new setOnDutyAsyncTask(ta, callBack).execute();
    }

    public void setOffDutyInBackground(TA ta, UpdateDutyCallBack callBack)
    {
        progressDialog.show();
        new setOffDutyAsyncTask(ta, callBack).execute();
    }

    public void getOnDutyTAInBackground(TA ta, getOnDutyCallBack callBack)
    {
        progressDialog.show();
        new getOnDutyTAAsyncTask(ta, callBack).execute();
    }

    public void insertRegIdInBackground(String regId, String username, GetRegIdCallBack callBack) {
        progressDialog.show();
        new insertRegIdAsyncTask(regId, username, callBack).execute();
    }

    public void sendTALogInInBackground(String message, SendTALogInCallBack callBack) {
        progressDialog.show();
        new sendTALogInAsyncTask(message, callBack).execute();
    }

    public class StoreUserDateAsyncTask extends AsyncTask<Void, Void, Void>
    {
        User user;
        GetUserCallBack userCallback;

        public StoreUserDateAsyncTask(User user, GetUserCallBack userCallback)
        {
            this.user = user;
            this.userCallback = userCallback;
        }

        private String getEncodedData(Map<String,String> data) {
            return ServerRequests.this.getEncodedData(data);
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            Map dataToSend = new HashMap();
            dataToSend.put("name", user.name);
            dataToSend.put("username", user.username);
            dataToSend.put("password", user.password);

            String encodedStr = getEncodedData(dataToSend);
            BufferedReader reader = null;
            String filename = "Register.php";

            sendDataToDatabase(encodedStr, filename, null);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            progressDialog.dismiss();
            userCallback.done(null);

            super.onPostExecute(aVoid);
        }
    }

    public class fetchUserDateAsyncTask extends AsyncTask<Void, Void, User> {
        User user;
        GetUserCallBack userCallback;

        //TODO: Implement this to work and refactor doInBackground
        onDatabaseReturnCallBack databaseCallBack = new onDatabaseReturnCallBack() {
            @Override
            public void DatabaseReturnExecute(String line) {
                try {
                    if (line.equals("[]")) {
                        user = null;
                    } else {
                        JSONObject jObject = new JSONObject(line);
                        String name = jObject.getString("name");
                        user = new User(name, user.username, user.password);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        public fetchUserDateAsyncTask(User user, GetUserCallBack userCallback) {
            this.user = user;
            this.userCallback = userCallback;
        }

        private String getEncodedData(Map<String,String> data) {
            return ServerRequests.this.getEncodedData(data);
        }

        @Override
        protected User doInBackground(Void... params) {
            Map dataToSend = new HashMap();
            dataToSend.put("username", user.username);
            dataToSend.put("password", user.password);

            String encodedStr = getEncodedData(dataToSend);

            String filename = "FetchUserData.php";

            sendDataToDatabase(encodedStr, filename, databaseCallBack);

            return user;
        }

        @Override
        protected void onPostExecute(User returnedUser) {
            progressDialog.dismiss();
            userCallback.done(returnedUser);
            super.onPostExecute(returnedUser);
        }
    }

    public class checkUserDateAsyncTask extends AsyncTask<Void, Void, User> {
        User user;
        GetUserCallBack userCallback;

        onDatabaseReturnCallBack databaseCallback = new onDatabaseReturnCallBack() {
            @Override
            //TODO: figure out if i need to make a new user: returnUser or not
            public void DatabaseReturnExecute(String line) {
                if (line.equals("[]"))
                {
                    //user = new User(user.name, user.username, user.password);
                }
                else
                {
                    user = null;
                }
            }
        };

        public checkUserDateAsyncTask(User user, GetUserCallBack userCallback) {
            this.user = user;
            this.userCallback = userCallback;
        }

        private String getEncodedData(Map<String,String> data) {
            return ServerRequests.this.getEncodedData(data);
        }

        @Override
        protected User doInBackground(Void... params) {
            Map dataToSend = new HashMap();
            dataToSend.put("username", user.username);

            String encodedStr = getEncodedData(dataToSend);

            String filename = "CheckUserData.php";

            sendDataToDatabase(encodedStr, filename, databaseCallback);

            return user;
        }

        @Override
        protected void onPostExecute(User returnedUser) {
            progressDialog.dismiss();
            userCallback.done(returnedUser);
            super.onPostExecute(returnedUser);
        }
    }

    public class getAllCourseAsyncTask extends AsyncTask<Void, Void, Map> {
        User user;
        GetCourseCallBack CourseCallback;
        Map returnCourse = new HashMap();

        onDatabaseReturnCallBack databaseCallback = new onDatabaseReturnCallBack() {
            @Override
            public void DatabaseReturnExecute(String line) {
                if (!line.equals("[]"))
                {
                    //split json string to map
                    line = line.substring(1);
                    line = line.substring(0, line.length() - 1);
                    String[] temp_list = line.split(",");
                    for (int i = 0; i < temp_list.length; i++)
                    {
                        String new_element = temp_list[i];
                        new_element = new_element.substring(1);
                        new_element = new_element.substring(0, new_element.length() - 1);
                        String temp[] = new_element.split(":");
                        String key = temp[0];
                        key = key.substring(1);
                        key = key.substring(0, key.length() - 1);
                        String value = temp[1];
                        value = value.substring(1);
                        value = value.substring(0, value.length() - 1);
                        returnCourse.put(key, value);
                    }
                }
            }
        };

        public getAllCourseAsyncTask(User user, GetCourseCallBack CourseCallback) {
            this.user = user;
            this.CourseCallback = CourseCallback;
        }

        private String getEncodedData(Map<String,String> data) {
            return ServerRequests.this.getEncodedData(data);
        }

        @Override
        protected Map doInBackground(Void... params) {
            Map dataToSend = new HashMap();
            dataToSend.put("username", user.username);

            String encodedStr = getEncodedData(dataToSend);
            String filename = "GetAllCourse.php";

            sendDataToDatabase(encodedStr, filename, databaseCallback);

            return returnCourse;
        }

        @Override
        protected void onPostExecute(Map returnCourse) {
            progressDialog.dismiss();
            CourseCallback.done(returnCourse);
            super.onPostExecute(returnCourse);
        }
    }

    public class getAllCourseDescriptionAsyncTask extends AsyncTask<Void, Void, String> {
        Course course;
        GetDescriptionCallBack DescriptionCallback;
        String description;

        onDatabaseReturnCallBack databaseCallback = new onDatabaseReturnCallBack() {
            @Override
            public void DatabaseReturnExecute(String line) {
                if (!line.equals("[]"))
                {
                    line = line.substring(1);
                    line = line.substring(0, line.length() - 1);
                    String[] content = line.split(":");
                    description = content[1];
                    description = description.substring(1);
                    description = description.substring(0, description.length() - 1);
                }
            }
        };

        public getAllCourseDescriptionAsyncTask(Course course, GetDescriptionCallBack DescriptionCallback) {
            this.course = course;
            this.DescriptionCallback = DescriptionCallback;
            description = new String();
        }

        private String getEncodedData(Map<String,String> data) {
            return ServerRequests.this.getEncodedData(data);
        }

        @Override
        protected String doInBackground(Void... params) {
            Map dataToSend = new HashMap();
            dataToSend.put("course_name", course.course_name);

            String encodedStr = getEncodedData(dataToSend);
            String filename = "GetSelectedCourse.php";

            sendDataToDatabase(encodedStr, filename, databaseCallback);

            return description;
        }

        @Override
        protected void onPostExecute(String returnDescription) {
            progressDialog.dismiss();
            DescriptionCallback.done(returnDescription);
            super.onPostExecute(returnDescription);
        }
    }

    public class CreateQueueAsyncTask extends AsyncTask<Void, Void, Void> {
        Queue queue;
        GetActualQueueCallBack ActualQueueCallback;

        public CreateQueueAsyncTask(Queue queue, GetActualQueueCallBack ActualQueueCallback) {
            this.queue = queue;
            this.ActualQueueCallback = ActualQueueCallback;
        }

        private String getEncodedData(Map<String,String> data) {
            return ServerRequests.this.getEncodedData(data);
        }

        @Override
        protected Void doInBackground(Void... params) {
            Map dataToSend = new HashMap();
            dataToSend.put("course_name", queue.course_name);
            dataToSend.put("queue_name", queue.queue_name);
            String encodedStr = getEncodedData(dataToSend);
            String filename = "CreateQueue.php";

            sendDataToDatabase(encodedStr, filename, null);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            ActualQueueCallback.done(null);
            super.onPostExecute(aVoid);
        }
    }

    public class getAllQueueAsyncTask extends AsyncTask<Void, Void, ArrayList> {
        Question question;
        GetQueueCallBack QueueCallback;
        ArrayList queue = new ArrayList();

        onDatabaseReturnCallBack databaseCallback = new onDatabaseReturnCallBack() {
            @Override
            public void DatabaseReturnExecute(String line) {
                if (!line.equals("null")) {
                    line = line.substring(1);
                    line = line.substring(0, line.length() - 1);
                    String[] temp_list = line.split("],");
                    for (int i = 0; i < temp_list.length; i++) {
                        String new_element = temp_list[i];
                        if (new_element.charAt(0) == '[')
                            new_element = new_element.substring(1);
                        if (new_element.charAt(new_element.length() - 1) == ']')
                            new_element = new_element.substring(0, new_element.length() - 1);
                        new_element = new_element.substring(1);
                        new_element = new_element.substring(0, new_element.length() - 1);
                        String temp_info[] = new_element.split(",");
                        Map user_info = new HashMap();
                        for (int j = 0; j < temp_info.length; j++) {
                            String new_info = temp_info[j];
                            String temp[] = new_info.split(":");
                            String key = temp[0];
                            key = key.substring(1);
                            key = key.substring(0, key.length() - 1);
                            String value = temp[1];
                            value = value.substring(1);
                            value = value.substring(0, value.length() - 1);
                            user_info.put(key, value);
                        }

                        queue.add(user_info);
                    }
                }
            }
        };

        public getAllQueueAsyncTask(Question question, GetQueueCallBack QueueCallback) {
            this.question = question;
            this.QueueCallback = QueueCallback;
        }

        private String getEncodedData(Map<String,String> data) {
            return ServerRequests.this.getEncodedData(data);
        }

        @Override
        protected ArrayList doInBackground(Void... params) {
            Map dataToSend = new HashMap();
            dataToSend.put("course_name", question.course_name);

            String encodedStr = getEncodedData(dataToSend);
            Log.i("send_check", "the data sent is: " + encodedStr);

            String filename = "GetAllQueue.php";

            sendDataToDatabase(encodedStr, filename, databaseCallback);
            return queue;
        }

        @Override
        protected void onPostExecute(ArrayList returnQueue) {
            progressDialog.dismiss();
            QueueCallback.done(returnQueue);
            super.onPostExecute(returnQueue);
        }
    }

    public class InsertQueueAsyncTask extends AsyncTask<Void, Void, Void> {
        Question question;
        GetQueueCallBack QueueCallback;

        public InsertQueueAsyncTask(Question question, GetQueueCallBack QueueCallback) {
            this.question = question;
            this.QueueCallback = QueueCallback;
        }

        private String getEncodedData(Map<String,String> data) {
            return ServerRequests.this.getEncodedData(data);
        }

        @Override
        protected Void doInBackground(Void... params) {
            Map dataToSend = new HashMap();
            dataToSend.put("user_name", question.user_name);
            dataToSend.put("user_pos", question.user_pos);
            dataToSend.put("user_topic", question.user_topic);
            dataToSend.put("user_notes", question.user_notes); // foobar
            dataToSend.put("course_name", question.course_name);
            dataToSend.put("ta_id", question.ta_id);
            String encodedStr = getEncodedData(dataToSend);

            String filename = "Enqueue.php";

            sendDataToDatabase(encodedStr, filename, null);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            QueueCallback.done(null);
            super.onPostExecute(aVoid);
        }
    }

    public class EditQueueAsyncTask extends AsyncTask<Void, Void, Void> {
        Question question;
        GetQueueCallBack QueueCallback;

        public EditQueueAsyncTask(Question question, GetQueueCallBack QueueCallback) {
            this.question = question;
            this.QueueCallback = QueueCallback;
        }

        private String getEncodedData(Map<String,String> data) {
            return ServerRequests.this.getEncodedData(data);
        }

        @Override
        protected Void doInBackground(Void... params) {
            Map dataToSend = new HashMap();
            dataToSend.put("user_name", question.user_name);
            dataToSend.put("user_pos", question.user_pos);
            dataToSend.put("user_topic", question.user_topic);
            dataToSend.put("user_notes", question.user_notes); // foobar
            dataToSend.put("course_name", question.course_name);
            dataToSend.put("ta_id", question.ta_id);
            String encodedStr = getEncodedData(dataToSend);

            String filename = "Editqueue.php";

            sendDataToDatabase(encodedStr, filename, null);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            QueueCallback.done(null);
            super.onPostExecute(aVoid);
        }
    }

    public class ClearQueueAsyncTask extends AsyncTask<Void, Void, Void> {
        Question question;
        GetQueueCallBack QueueCallback;

        public ClearQueueAsyncTask(Question question, GetQueueCallBack QueueCallback) {
            this.question = question;
            this.QueueCallback = QueueCallback;
        }

        private String getEncodedData(Map<String,String> data) {
            return ServerRequests.this.getEncodedData(data);
        }

        @Override
        protected Void doInBackground(Void... params) {
            Map dataToSend = new HashMap();
            dataToSend.put("course_name", question.course_name);
            String encodedStr = getEncodedData(dataToSend);

            String filename = "clearQueue.php";

            sendDataToDatabase(encodedStr, filename, null);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            QueueCallback.done(null);
            super.onPostExecute(aVoid);
        }
    }

    public class DeleteQueueAsyncTask extends AsyncTask<Void, Void, Void> {
        Question question;
        GetQueueCallBack QueueCallback;

        public DeleteQueueAsyncTask(Question question, GetQueueCallBack QueueCallback) {
            this.question = question;
            this.QueueCallback = QueueCallback;
        }

        private String getEncodedData(Map<String,String> data) {
            return ServerRequests.this.getEncodedData(data);
        }

        @Override
        protected Void doInBackground(Void... params) {
            Map dataToSend = new HashMap();
            dataToSend.put("user_name", question.user_name);
            dataToSend.put("course_name", question.course_name);
            String encodedStr = getEncodedData(dataToSend);

            String filename = "Dequeue.php";

            sendDataToDatabase(encodedStr, filename, null);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            QueueCallback.done(null);
            super.onPostExecute(aVoid);
        }
    }

    /**
     * Insert a grade into the DB.
     */
    public class InsertGradeAsyncTask extends AsyncTask<Void, Void, Void> {
        Grade grade;
        GetGradeCallBack GradeCallback;

        public InsertGradeAsyncTask(Grade grade, GetGradeCallBack GradeCallback) {
            this.grade = grade;
            this.GradeCallback = GradeCallback;
        }

        private String getEncodedData(Map<String,String> data) {
            return ServerRequests.this.getEncodedData(data);
        }

        @Override
        protected Void doInBackground(Void... params) {
            Map dataToSend = new HashMap();
            dataToSend.put("username", grade.username);
            dataToSend.put("score", grade.score);
            dataToSend.put("description", grade.description);
            String encodedStr = getEncodedData(dataToSend);

            String filename = "AddGrade.php";

            sendDataToDatabase(encodedStr, filename, null);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            GradeCallback.done(null);
            super.onPostExecute(aVoid);
        }
    }

    /**
     * Get a list of all grades from the DB.
     */
    public class GetGradeAsyncTask extends AsyncTask<Void, Void, ArrayList> {
        User user;
        GetGradeCallBack GradeCallback;
        ArrayList grades = new ArrayList();

        onDatabaseReturnCallBack databaseCallback = new onDatabaseReturnCallBack() {
            @Override
            public void DatabaseReturnExecute(String line) {
                if (!line.equals("null"))
                {
                    line = line.substring(1);
                    line = line.substring(0, line.length() - 1);
                    String[] temp_list = line.split("],");
                    for (int i = 0; i < temp_list.length; i++)
                    {
                        String new_element = temp_list[i];
                        if (new_element.charAt(0) == '[')
                            new_element = new_element.substring(1);
                        if (new_element.charAt(new_element.length()-1) == ']')
                            new_element = new_element.substring(0, new_element.length() - 1);
                        new_element = new_element.substring(1);
                        new_element = new_element.substring(0, new_element.length() - 1);
                        String temp_info[] = new_element.split(",");
                        Map user_info = new HashMap();
                        for (int j = 0; j < temp_info.length; j++)
                        {
                            String new_info = temp_info[j];
                            String temp[] = new_info.split(":");
                            String key = temp[0];
                            key = key.substring(1);
                            key = key.substring(0, key.length() - 1);
                            String value = temp[1];

                            // If it's not a number, take off the " characters at the beginning and end
                            if (!key.equals("score")) {
                                value = value.substring(1);
                                value = value.substring(0, value.length() - 1);
                            }

                            user_info.put(key, value);
                        }

                        grades.add(user_info);
                    }
                }
            }
        };

        public GetGradeAsyncTask(User user, GetGradeCallBack GradeCallback) {
            this.user = user;
            this.GradeCallback = GradeCallback;
        }

        private String getEncodedData(Map<String,String> data) {
            return ServerRequests.this.getEncodedData(data);
        }

        @Override
        protected ArrayList doInBackground(Void... params) {
            Map dataToSend = new HashMap();
            dataToSend.put("username", user.username);
            String encodedStr = getEncodedData(dataToSend);
            Log.i("user_check", "the data sent is: " + encodedStr);

            String filename = "GetUserGrades.php";

            sendDataToDatabase(encodedStr, filename, databaseCallback);
            return grades;
        }

        @Override
        protected void onPostExecute(ArrayList returnGrade) {
            progressDialog.dismiss();
            GradeCallback.done(returnGrade);
            super.onPostExecute(returnGrade);
        }
    }

    public class CheckAuthorisationAsyncTask extends AsyncTask<Void, Void, String> {
        TA ta;
        CheckAuthorisationCallBack TaCallback;
        String ta_info = "";

        onDatabaseReturnCallBack databaseCallback = new onDatabaseReturnCallBack() {
            @Override
            public void DatabaseReturnExecute(String line) {
                if (!line.equals("[]"))
                {
                    ta_info = line;
                }
            }
        };

        public CheckAuthorisationAsyncTask(TA ta, CheckAuthorisationCallBack TaCallback) {
            this.ta = ta;
            this.TaCallback = TaCallback;
        }

        private String getEncodedData(Map<String,String> data) {
            return ServerRequests.this.getEncodedData(data);
        }

        @Override
        protected String doInBackground(Void... params) {
            Map dataToSend = new HashMap();
            dataToSend.put("name", ta.name);
            dataToSend.put("course_name", ta.course_name);
            String encodedStr = getEncodedData(dataToSend);

            String filename = "CheckAuthorisation.php";

            sendDataToDatabase(encodedStr, filename, databaseCallback);
            return ta_info;
        }

        @Override
        protected void onPostExecute(String ta_info) {
            progressDialog.dismiss();
            TaCallback.done(ta_info);
            super.onPostExecute(ta_info);
        }
    }

    public class answerQuestionAsyncTask extends AsyncTask<Void, Void, Void> {
        Question question;
        GetQueueCallBack QueueCallBack;

        public answerQuestionAsyncTask(Question question, GetQueueCallBack QueueCallBack) {
            this.question = question;
            this.QueueCallBack = QueueCallBack;
        }

        private String getEncodedData(Map<String,String> data) {
            return ServerRequests.this.getEncodedData(data);
        }

        @Override
        protected Void doInBackground(Void... params) {
            Map dataToSend = new HashMap();
            dataToSend.put("user_name", question.user_name);
            dataToSend.put("course_name", question.course_name);
            dataToSend.put("ta_id", question.ta_id);
            String encodedStr = getEncodedData(dataToSend);

            String filename = "answer.php";

            sendDataToDatabase(encodedStr, filename, null);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            QueueCallBack.done(null);
            super.onPostExecute(aVoid);
        }
    }

    public class setOnDutyAsyncTask extends AsyncTask<Void, Void, String> {
        TA ta;
        UpdateDutyCallBack TaCallback;
        String ta_info = "";

        onDatabaseReturnCallBack databaseCallback = new onDatabaseReturnCallBack() {
            @Override
            public void DatabaseReturnExecute(String line) {
                if (!line.equals("[]"))
                {
                    ta_info = line;
                }
            }
        };

        public setOnDutyAsyncTask(TA ta, UpdateDutyCallBack TaCallback) {
            this.ta = ta;
            this.TaCallback = TaCallback;
        }

        private String getEncodedData(Map<String,String> data) {
           return ServerRequests.this.getEncodedData(data);
        }

        @Override
        protected String doInBackground(Void... params) {
            Map dataToSend = new HashMap();
            dataToSend.put("name", ta.name);
            dataToSend.put("course_name", ta.course_name);
            String encodedStr = getEncodedData(dataToSend);
            String filename = "Onduty.php";

            sendDataToDatabase(encodedStr, filename, databaseCallback);
            return ta_info;
        }

        @Override
        protected void onPostExecute(String ta_info) {
            progressDialog.dismiss();
            TaCallback.done(ta_info);
            super.onPostExecute(ta_info);
        }
    }

    public class setOffDutyAsyncTask extends AsyncTask<Void, Void, String> {
        TA ta;
        UpdateDutyCallBack TaCallback;
        String ta_info = "";

        onDatabaseReturnCallBack databaseCallback = new onDatabaseReturnCallBack() {
            @Override
            public void DatabaseReturnExecute(String line) {
                if (!line.equals("[]"))
                {
                    ta_info = line;
                }
            }
        };

        public setOffDutyAsyncTask(TA ta, UpdateDutyCallBack TaCallback) {
            this.ta = ta;
            this.TaCallback = TaCallback;
        }

        private String getEncodedData(Map<String,String> data) {
            return ServerRequests.this.getEncodedData(data);
        }

        @Override
        protected String doInBackground(Void... params) {
            Map dataToSend = new HashMap();
            dataToSend.put("name", ta.name);
            dataToSend.put("course_name", ta.course_name);
            System.out.println("check" + ta.name);
            String encodedStr = getEncodedData(dataToSend);

            String filename = "Offduty.php";

            sendDataToDatabase(encodedStr, filename, databaseCallback);
            return ta_info;
        }

        @Override
        protected void onPostExecute(String ta_info) {
            progressDialog.dismiss();
            TaCallback.done(ta_info);
            super.onPostExecute(ta_info);
        }
    }

    //TODO: Due to the ta_list being populated inside the while loop, i didn't refactor this
    //TODO: Actually change functionality so that this can call sendDataToDatabase
    public class getOnDutyTAAsyncTask extends AsyncTask<Void, Void, String[]> {
        TA ta;
        getOnDutyCallBack TaCallback;

        public getOnDutyTAAsyncTask(TA ta, getOnDutyCallBack TaCallback) {
            this.ta = ta;
            this.TaCallback = TaCallback;
        }

        private String getEncodedData(Map<String,String> data) {
            return ServerRequests.this.getEncodedData(data);
        }

        @Override
        protected String[] doInBackground(Void... params) {
            Map dataToSend = new HashMap();
            dataToSend.put("course_name", ta.course_name);
            String encodedStr = getEncodedData(dataToSend);
            BufferedReader reader = null;
            String[] ta_list = new String[20];
            String filename = "GetOnDutyTA.php";

            try {
                HttpURLConnection con = setupServer(filename, encodedStr);
                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line;
                int index = 0;
                while((line = reader.readLine()) != null) {
                    sb.append(line);
                    ta_list[index] = line.toString();
                    index++;
                }
                line = sb.toString();

                Log.i("custom_check","The values received in the store part are as follows:");
                Log.i("custom_check",line);

                if (line.equals(""))
                {
                   ta_list = new String[20];
                }
                else
                {
                    
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(reader != null) {
                    try {
                        reader.close();     //Closing the
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return ta_list;
        }

        @Override
        protected void onPostExecute(String[] ta_list) {
            progressDialog.dismiss();
            TaCallback.done(ta_list);
            super.onPostExecute(ta_list);
        }
    }

    /**
     * Insert a regid into the DB.
     */
    public class insertRegIdAsyncTask extends AsyncTask<Void, Void, Void> {
        String id;
        String username;
        GetRegIdCallBack RegIdCallback;

        public insertRegIdAsyncTask(String id, String username, GetRegIdCallBack RegIdCallback) {
            this.id = id;
            this.username = username;
            this.RegIdCallback = RegIdCallback;
        }

        private String getEncodedData(Map<String,String> data) {
            return ServerRequests.this.getEncodedData(data);
        }

        @Override
        protected Void doInBackground(Void... params) {
            Map dataToSend = new HashMap();
            dataToSend.put("id", id);
            dataToSend.put("username", username);
            String encodedStr = getEncodedData(dataToSend);
            String filename = "AddRegId.php";

            sendDataToDatabase(encodedStr, filename, null);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            RegIdCallback.done("");
            super.onPostExecute(aVoid);
        }
    }

    public class sendTALogInAsyncTask extends AsyncTask<Void, Void, Void> {
        String message;
        SendTALogInCallBack sendLoginCallback;

        public sendTALogInAsyncTask(String message, SendTALogInCallBack sendLoginCallback) {
            this.message = message;
            this.sendLoginCallback = sendLoginCallback;
        }

        private String getEncodedData(Map<String,String> data) {
            return ServerRequests.this.getEncodedData(data);
        }

        @Override
        protected Void doInBackground(Void... params) {
            Map dataToSend = new HashMap();
            dataToSend.put("message", message);
            String encodedStr = getEncodedData(dataToSend);
            String filename = "gcm_ta_login.php";

            sendDataToDatabase(encodedStr, filename, null);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            sendLoginCallback.done(null);
            super.onPostExecute(aVoid);
        }
    }

}
