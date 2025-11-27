package timefit.common.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 이메일 발송 서비스
 *
 * [목적]
 * HTML 이메일을 비동기로 발송하여 API 응답 속도를 개선합니다.
 *
 * [현재 사용처]
 * - InvitationCommandService.sendInvitationEmail() : 초대 이메일 발송
 *
 * [비동기 처리]
 * AsyncConfig에서 설정한 스레드 풀을 사용하여 백그라운드에서 이메일을 발송합니다.
 * 이메일 발송 실패 시 EmailSendException이 발생하지만, 비동기 메서드이므로
 * 호출자에게 전파되지 않고 로그로만 기록됩니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * HTML 이메일 발송 (비동기)
     *
     * [동작 방식]
     * 1. 요청 스레드에서 즉시 반환
     * 2. AsyncConfig의 스레드 풀에서 실제 발송 처리
     * 3. 발송 성공/실패 로그 기록
     *
     * @param to          수신자 이메일
     * @param subject     제목
     * @param htmlContent HTML 본문
     */
    @Async
    public void sendHtmlEmailAsync(String to, String subject, String htmlContent) {
        sendHtmlEmail(to, subject, htmlContent);
    }

    /**
     * HTML 이메일 발송 (내부 전용)
     * - sendHtmlEmailAsync에서 호출됨
     * - Gmail SMTP를 통해 발송
     *
     * @param to          수신자 이메일
     * @param subject     제목
     * @param htmlContent HTML 본문
     * @throws EmailSendException 발송 실패 시
     */
    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        log.info("HTML 이메일 발송 시작 - 수신자: {}, 제목: {}", to, subject);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);  // true = HTML

            mailSender.send(mimeMessage);
            log.info("HTML 이메일 발송 성공 - 수신자: {}", to);
        } catch (MessagingException e) {
            log.error("HTML 이메일 발송 실패 - 수신자: {}, 오류: {}", to, e.getMessage(), e);
            throw new EmailSendException("HTML 이메일 발송에 실패했습니다: " + to, e);
        }
    }
}