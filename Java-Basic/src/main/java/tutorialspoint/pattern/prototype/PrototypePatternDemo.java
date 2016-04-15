package tutorialspoint.pattern.prototype;
public class PrototypePatternDemo {
   public static void main(String[] args) {
	   
   // Creating all object need to be cloned to use at the next time
      ShapeCache.loadCache();
      
   // get cloned object from Cache
      Shape clonedShape = (Shape) ShapeCache.getShape("1");
      System.out.println("Shape : " + clonedShape.getType());		

   // get cloned object from Cache
      Shape clonedShape2 = (Shape) ShapeCache.getShape("2");
      System.out.println("Shape : " + clonedShape2.getType());		

   // get cloned object from Cache
      Shape clonedShape3 = (Shape) ShapeCache.getShape("3");
      System.out.println("Shape : " + clonedShape3.getType());		
   }
}