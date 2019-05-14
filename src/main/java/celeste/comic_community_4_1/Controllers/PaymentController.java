package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.config.PaypalPaymentIntent;
import celeste.comic_community_4_1.config.PaypalPaymentMethod;
import celeste.comic_community_4_1.exception.ResourceNotFoundException;
import celeste.comic_community_4_1.model.User;
import celeste.comic_community_4_1.repository.UserRepository;
import celeste.comic_community_4_1.service.PaypalService;
import celeste.comic_community_4_1.util.URLUtils;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/paypal_checkout")
public class PaymentController {

    public static final String PAYPAL_SUCCESS_URL = "pay/pay_success";
    public static final String PAYPAL_CANCEL_URL = "pay/pay_cancel";

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private PaypalService paypalService;

    @Autowired
    UserRepository userRepository;


    @RequestMapping(method = RequestMethod.GET)
    public String index() {
        return "paypal";
    }

    @RequestMapping(method = RequestMethod.POST, value = "pay")
    public String pay(HttpServletRequest request) {
        String cancelUrl = URLUtils.getBaseURl(request) + "/" + PAYPAL_CANCEL_URL;
        String successUrl = URLUtils.getBaseURl(request) + "/" + PAYPAL_SUCCESS_URL;
        try {
            Payment payment = paypalService.createPayment(
                    9.99,
                    "USD",
                    PaypalPaymentMethod.paypal,
                    PaypalPaymentIntent.sale,
                    "payment description",
                    cancelUrl,
                    successUrl);
            for (Links links : payment.getLinks()) {
                if (links.getRel().equals("approval_url")) {
                    return "redirect:" + links.getHref();
                }
            }
        } catch (PayPalRESTException e) {
            log.error(e.getMessage());
        }
        return "redirect:/";
    }
//
//    @GetMapping("/pay/pay_cancel")
//    public String cancelPay() {
//        return "pay_cancel";
//    }

    @GetMapping("/pay/pay_success")
    public String successPay(@RequestParam("paymentId") String paymentId,
                             @RequestParam("PayerID") String payerId,
                             ModelMap model,
                             HttpServletRequest request) {
        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);
            if (payment.getState().equals("approved")) {

                // Blocked User
                user.setMembership("1");
                userRepository.save(user);
            }
        } catch (PayPalRESTException e) {
            log.error(e.getMessage());
        }

        model.addAttribute("User", user);
        return "setting";
    }

}
