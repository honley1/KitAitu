package com.honley.kitaitu.util;

import android.content.Context;
import android.net.Uri;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.honley.kitaitu.request.SmsLoginRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public class HttpRequest {

    interface Requests {
        @GET("/api/students/student-card")
        Call<JsonElement> getStudentCard(@Header("Authorization") String bearerToken);

        @POST("/api/students/login-phone")
        Call<JsonElement> loginPhone(@Body JsonElement phone);

        @POST("/api/students/login-sms-code")
        Call<JsonElement> loginSmsCode(@Body SmsLoginRequest request);

        @GET("/{fileName}")
        Call<ResponseBody> getFile(@Path("fileName") String fileName);

        @GET("/api/news/get-news")
        Call<JsonElement> getNews(@Header("Authorization") String bearerToken);

        @GET("/api/news/{title}")
        Call<JsonElement> getNewsByTitle(@Header("Authorization") String bearerToken, @Path("title") String title);

        @GET("/api/announcements/get-announcements")
        Call<JsonElement> getAnnouncements(@Header("Authorization") String bearerToken);

        @GET("/api/announcements/{title}")
        Call<JsonElement> getAnnouncementByTitle(@Header("Authorization") String bearerToken, @Path("title") String title);

        @POST("/api/appeal/create-appeal")
        Call<JsonElement> createAppeal(@Header("Authorization") String bearerToken, @Body JsonElement content);

        @Multipart
        @POST("/api/students/create-avatar")
        Call<JsonElement> createAvatar(@Header("Authorization") String bearerToken, @Part MultipartBody.Part avatar);
    }

    public interface ApiCallback {
        void onSuccess(String result);
        void onError(String error);
    }

    public interface ApiCallbackFile {
        void onSuccess(File result);
        void onError(String error);
    }

    private static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://192.168.10.15:5000")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private static final Requests requests = retrofit.create(Requests.class);

    public static void getStudentCard(String token, ApiCallback callback) {
        String bearerToken = "Bearer " + token;

        requests.getStudentCard(bearerToken).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    JsonElement data = response.body();
                    callback.onSuccess(data != null ? data.toString() : null);
                } else {
                    callback.onError("Error: " + response);
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                callback.onError("Request failed: " + t.getMessage());
            }
        });
    }

    public static void loginPhone(String phone, ApiCallback callback) {
        JsonObject jsonData = new JsonObject();
        jsonData.addProperty("phone", phone);

        requests.loginPhone(jsonData).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    JsonElement data = response.body();
                    callback.onSuccess(data != null ? data.toString() : null);
                } else {
                    callback.onError("Error: " + response);
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                callback.onError("Request failed: " + t.getMessage());
            }
        });
    }

    public static void loginSmsCode(String phone, String smsCode, ApiCallback callback) {
        SmsLoginRequest request = new SmsLoginRequest(phone, smsCode);

        requests.loginSmsCode(request).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    JsonElement data = response.body();
                    callback.onSuccess(data != null ? data.toString() : null);
                } else {
                    callback.onError("Error: " + response);
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                callback.onError("Request failed: " + t.getMessage());

            }
        });
    }

    public static void getFile(String fileName, Context context, ApiCallbackFile callback) {
        requests.getFile(fileName).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();

                    if (responseBody != null) {
                        File file = new File(context.getFilesDir(), fileName);

                        try (InputStream inputStream = responseBody.byteStream();
                             FileOutputStream fileOutputStream = new FileOutputStream(file)) {

                            byte[] buffer = new byte[4096];
                            int bytesRead;

                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                fileOutputStream.write(buffer, 0, bytesRead);
                            }

                            callback.onSuccess(file);
                        } catch (IOException e) {
                            callback.onError(e.getMessage());
                        }
                    } else {
                        callback.onError(new Exception("Response body is null").getMessage());
                    }
                } else {
                    callback.onError(new Exception("Error: " + response.message()).getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onError("Request failed: " + t.getMessage());
            }
        });
    }

    public static void createAppeal(String token, String content, ApiCallback callback) {
        String bearerToken = "Bearer " + token;

        JsonObject jsonData = new JsonObject();
        jsonData.addProperty("content", content);

        requests.createAppeal(bearerToken, jsonData).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    JsonElement data = response.body();
                    callback.onSuccess(data != null ? data.toString() : null);
                } else {
                    callback.onError("Error: " + response);
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                callback.onError("Request failed: " + t.getMessage());
            }
        });
    }

    public static void getNews(String token, ApiCallback callback) {
        String bearerToken = "Bearer " + token;

        requests.getNews(bearerToken).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    JsonElement data = response.body();
                    callback.onSuccess(data != null ? data.toString() : null);
                } else {
                    System.out.println(response);
                    callback.onError("Error: " + response);
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                callback.onError("Request failed: " + t.getMessage());
            }
        });
    }

    public static void getNewsByTitle(String token, String title, ApiCallback callback) {
        String bearerToken = "Bearer " + token;

        requests.getNewsByTitle(bearerToken, title).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    JsonElement data = response.body();
                    callback.onSuccess(data != null ? data.toString() : null);
                } else {
                    callback.onError("Error: " + response);
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                callback.onError("Request failed: " + t.getMessage());
            }
        });
    }

    public static void getAnnouncements(String token, ApiCallback callback) {
        String bearerToken = "Bearer " + token;

        requests.getAnnouncements(bearerToken).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    JsonElement data = response.body();
                    callback.onSuccess(data != null ? data.toString() : null);
                } else {
                    callback.onError("Error: " + response);
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                callback.onError("Request failed: " + t.getMessage());
            }
        });
    }

    public static void getAnnouncementByTitle(String token, String title, ApiCallback callback) {
        String bearerToken = "Bearer " + token;

        requests.getAnnouncementByTitle(bearerToken, title).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    JsonElement data = response.body();
                    callback.onSuccess(data != null ? data.toString() : null);
                } else {
                    callback.onError("Error: " + response);
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                callback.onError("Request failed: " + t.getMessage());
            }
        });
    }

    public static void createAvatar(String token, Uri avatar, Context context, ApiCallback callback) {
        String bearerToken = "Bearer " + token;

        try (InputStream inputStream = context.getContentResolver().openInputStream(avatar)) {
            File file = new File(context.getCacheDir(), "avatar_temp");

            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }
            }

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("avatar", file.getName(), requestFile);

            requests.createAvatar(bearerToken, body).enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                    if (response.isSuccessful()) {
                        JsonElement data = response.body();
                        callback.onSuccess(data != null ? data.toString() : null);
                    } else {
                        callback.onError("Error: " + response);
                    }
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    callback.onError("Request failed: " + t.getMessage());
                }
            });
        } catch (IOException e) {
            callback.onError("Error reading file: " + e.getMessage());
        }
    }

}
