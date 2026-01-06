package com.example.backend.model;

public class TenantContext {

    private static ThreadLocal<String> TEANAT_ID = new ThreadLocal<>();
    private static ThreadLocal<String> USER_ID = new ThreadLocal<>();

  public static void setTenantId(String id){
      if(id==null || id.isBlank()){
          throw new IllegalArgumentException("Tenant id cannot be blank");
      }
      TEANAT_ID.set(id);
  }

  public static String getTenentid(){
      String tenantid = TEANAT_ID.get();
      if(tenantid==null || tenantid.isBlank()){
          throw new IllegalArgumentException("Tenant id cannot be blank");
      }
      return tenantid;
  }

    public static void setUserId(String id){
      if(id==null || id.isBlank()){
          throw new IllegalArgumentException("Tenant id cannot be blank");
      }
      USER_ID.set(id);
    }

    public static String getUserId(){
      return USER_ID.get() !=null ? USER_ID.get(): "system";
    }

    public static void clear(){
      TEANAT_ID.remove();
      USER_ID.remove();
    }

}
