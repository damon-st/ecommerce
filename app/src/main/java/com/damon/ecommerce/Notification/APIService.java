package com.damon.ecommerce.Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAW-0cB7I:APA91bE--cMvUBw_4IKEEwWXDtfjfjJsMLHQ5DvCxtU6Xozv99R0PGpuaYPTPXE96VbjSh83hSrrJmSQ3ex84LcHZ8cReJrAUqDS4noIjJKOr1fcZUvOq_XGfmDhdjN6eaMjBVL3Rc-U"
            }
    )


    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}