package auth;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


public class AuthInterceptor extends HandlerInterceptorAdapter {
	/* Get actual class name to be printed on */
	//private static org.apache.log4j.Logger log = Logger.getLogger(AuthInterceptor.class);
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		//System.out.println("testing ------------------------");
		//PropertyConfigurator.configure("log4j.properties");

		//log.info("AuthInterceptor.preHandle");
		//log.info("AuthInterceptor");


		HttpSession session = request.getSession();
		boolean isLogined = session.getAttribute("isLogin") != null ? (Boolean) session
				.getAttribute("isLogin") : false;
		if (!isLogined){
			response.sendRedirect(request.getContextPath() + "/login");
		}
		return isLogined;
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		//log.info("AuthInterceptor.postHandle");
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		//log.info("AuthInterceptor.afterCompletion");
		
	}

}
