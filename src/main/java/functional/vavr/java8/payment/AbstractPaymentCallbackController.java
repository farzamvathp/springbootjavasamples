package functional.vavr.java8.payment;

import com.blito.enums.Response;
import com.blito.exceptions.AlreadyPaidException;
import com.blito.exceptions.ResourceNotFoundException;
import com.blito.repositories.BlitRepository;
import com.blito.resourceUtil.ResourceUtil;
import com.blito.rest.viewmodels.payments.BlitoPaymentResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.view.RedirectView;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

/**
 * @author Farzam Vatanzadeh
 * 12/3/17
 * Mailto : farzam.vat@gmail.com
 **/

public abstract class AbstractPaymentCallbackController {
    @Value("${serverAddress}")
    protected String serverAddress;
    @Value("${api.base.url}")
    protected String baseUrl;
    @Autowired
    protected PaymentService paymentService;
    @Autowired
    protected BlitRepository blitRepository;

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    private Logger log = LoggerFactory.getLogger(getClass());
    protected CompletionStage<RedirectView> completePayment(String token, Supplier<BlitoPaymentResult> supplier) {
        return CompletableFuture.supplyAsync(() -> paymentService.finalizingPayment(supplier.get()))
                .handle((blit,throwable) -> {
                if(throwable != null) {
                    log.error("******* ERROR IN PAYMENT FLOW '{}'",throwable.getCause());
                    return blitRepository.findByToken(token)
                            .map(b -> {
                                if(!(throwable.getCause() instanceof AlreadyPaidException)) {
                                    paymentService.setError(b);
                                }
                                RedirectView redirectView =
                                        new RedirectView(String.valueOf(new StringBuilder(getServerAddress()).append("/functional/vavr/java8/payment/").append(b.getTrackCode())));
                                redirectView.setStatusCode(HttpStatus.SEE_OTHER);
                                return redirectView;
                            })
                            .orElseThrow(() -> new ResourceNotFoundException(ResourceUtil.getMessage(Response.BLIT_NOT_FOUND)));
                }
                RedirectView redirectView =
                        new RedirectView(String.valueOf(new StringBuilder(getServerAddress()).append("/functional/vavr/java8/payment/").append(blit.getTrackCode())));
                redirectView.setStatusCode(HttpStatus.SEE_OTHER);
                return redirectView;
        });
    }
}
