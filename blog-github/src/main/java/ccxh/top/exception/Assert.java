package ccxh.top.exception;

public class Assert {
   public static void isNull(Object value,String message){
       if (value==null){
            throw new ServiceException(message);
       }
   }
}
