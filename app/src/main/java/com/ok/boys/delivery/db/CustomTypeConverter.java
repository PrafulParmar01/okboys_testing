package com.ok.boys.delivery.db;



import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ok.boys.delivery.base.api.login.model.FranchiseDetails;
import com.ok.boys.delivery.base.api.login.model.GenerateLoginResponse;
import com.ok.boys.delivery.base.api.login.model.LoginDataResponse;
import com.ok.boys.delivery.base.api.login.model.LoginOrderResponse;
import com.ok.boys.delivery.base.api.login.model.LoginResponse;
import com.ok.boys.delivery.base.api.login.model.UserDetails;
import com.ok.boys.delivery.base.api.login.model.UserStatus;
import java.lang.reflect.Type;
import java.util.ArrayList;

import androidx.room.TypeConverter;


public class CustomTypeConverter {


    @TypeConverter
    public static ArrayList<String> toStringArrayFromString(String value) {
        Type listType = new TypeToken<ArrayList<String>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromStringArrayToString(ArrayList<String> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }


    @TypeConverter
    public static GenerateLoginResponse fromStringToGenerateLoginResponse(String value) {
        Type listType = new TypeToken<GenerateLoginResponse>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromGenerateLoginResponseToString(GenerateLoginResponse list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }



    @TypeConverter
    public static LoginDataResponse fromStringToLoginDataResponse(String value) {
        Type listType = new TypeToken<LoginDataResponse>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromLoginDataResponseToString(LoginDataResponse list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }



    @TypeConverter
    public static LoginResponse fromStringToLoginResponse(String value) {
        Type listType = new TypeToken<LoginResponse>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromLoginResponseToString(LoginResponse list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }


    @TypeConverter
    public static UserStatus fromStringToUserStatus(String value) {
        Type listType = new TypeToken<UserStatus>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromUserStatusToString(UserStatus list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }


    @TypeConverter
    public static UserDetails fromStringToUserDetails(String value) {
        Type listType = new TypeToken<UserDetails>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromUserDetailsToString(UserDetails list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }


    @TypeConverter
    public static FranchiseDetails fromStringToFranchiseDetails(String value) {
        Type listType = new TypeToken<FranchiseDetails>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromFranchiseDetailsToString(FranchiseDetails list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    @TypeConverter
    public static LoginOrderResponse fromStringToLoginOrderResponse(String value) {
        Type listType = new TypeToken<LoginOrderResponse>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromLoginOrderResponseToString(LoginOrderResponse list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
