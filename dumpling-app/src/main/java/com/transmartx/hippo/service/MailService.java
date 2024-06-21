package com.transmartx.hippo.service;

import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author Kosne
 */
@Service
@Slf4j
public class MailService {

    public static final int PRIORITY__HIGHEST = 1;
    public static final int PRIORITY__LOWEST = 5;

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private FreeMarkerConfigurer configurer;

//    @PostConstruct
    public void init() {
        configurer.setTemplateLoaderPath("");
    }

   @Value("${mail.fromMail.addr}")
   private String from;

    public boolean send(String from, String replyTo, String to[], String cc[], String subject, String text) {
        boolean result = send(generateMsg(from, replyTo, to, cc, subject, text), null, null, null, 0);
        if (log.isInfoEnabled()) {
            log.info("emailService.send [{}] [{}] subject=" + subject + ",text=" + text, to, result);
        }
        return result;
    }

    public boolean send(String from, String replyTo, String to[], String cc[], String subject, String template, Map<String, Object> model, String attachment) {
        boolean result = send(generateMsg(from, replyTo, to, cc, subject, template), template, model, attachment, 0);
        if (log.isInfoEnabled()) {
            log.info("emailService.send [{}] [{}] subject=" + subject + ",template=" + template + ",model=" + model, to, result);
        }
        return result;
    }

    public boolean send(String from, String replyTo, String to[], String cc[], String subject, String text, int priority) {
        boolean result = send(generateMsg(from, replyTo, to, cc, subject, text), null, null, null, priority);
        if (log.isInfoEnabled()) {
            log.info("emailService.send [{}] [{}] subject=" + subject + ",text=" + text + ",priority=" + priority, to, result);
        }
        return result;
    }

    public boolean send(String from, String replyTo, String to[], String cc[], String subject, String template, Map<String, Object> model, String attachment, int priority) {
        boolean result = send(generateMsg(from, replyTo, to, cc, subject, template), template, model, attachment, priority);
        if (log.isInfoEnabled()) {
            log.info("emailService.send [{}] [{}] subject=" + subject + ",template=" + template + ",model=" + model + ",priority=" + priority, to, result);
        }
        return result;
    }

    public boolean send(SimpleMailMessage msg, String template, Map<String, Object> model, String attachment, int priority) {
        try {
            MimeMessage mimeMsg = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMsg, true);

            String from = msg.getFrom();
            String replyTo = msg.getReplyTo();
            if (from != null && replyTo != null) {
                helper.setFrom(replyTo, from);
            } else if (replyTo != null) {
                helper.setFrom(replyTo);
            } else if (from != null) {
                helper.setFrom(from);
            }

            helper.setTo(msg.getTo());

            String[] cc = msg.getCc();
            if (cc != null) {
                helper.setCc(cc);
            }

            helper.setSubject(msg.getSubject());

            String text = null;
            if (template != null) {
                text = generateText(template, model);
            }
            if (text == null) {
                text = msg.getText();
            }
            helper.setText(text, true);

            if (attachment != null) {
                File file = (File) model.get(attachment);
                if (file != null) {
                    helper.addAttachment(MimeUtility.encodeWord(attachment), file);
                }
            }

            if (priority >= PRIORITY__HIGHEST && priority <= PRIORITY__LOWEST) {
                helper.setPriority(priority);
            }

            emailSender.send(mimeMsg);
            return true;
        } catch (Exception e) {
            log.error("send error [" + e.getMessage() + "] msg=" + msg + ",template=" + template + ",model=" + model + ",attachment=" + attachment, e);
            return false;
        }
    }

    private SimpleMailMessage generateMsg(String from, String replyTo, String to[], String cc[], String subject, String text) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setReplyTo(replyTo);
        msg.setTo(to);
        if (cc != null) {
            msg.setCc(cc);
        }
        msg.setSubject(subject);
        msg.setText(text);
        return msg;
    }

    @SuppressWarnings("rawtypes")
    private String generateText(String template, Map model) {
        try {
            return FreeMarkerTemplateUtils.processTemplateIntoString(configurer.getConfiguration().getTemplate(template), model);
        } catch (Exception e) {
            log.error("generateText error [" + e.getMessage() + "] template=" + template + ",model=" + model, e);
        }
        return null;
    }

    public void send(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        emailSender.send(message);
    }

    public void send(List<String> to, String subject, String content, List<File> attachments) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(from);
            if (to != null && to.size() > 0) {
                helper.setTo(to.toArray(new String[0]));
            }
            helper.setSubject(subject);
            helper.setText(content);

            if (!CollectionUtils.isEmpty(attachments)) {
                for (File file : attachments) {
                	// 解决附件文件名乱码
                    helper.addAttachment(MimeUtility.encodeText(file.getName(),"UTF-8","B"), file);
                }
            }

            emailSender.send(message);
        } catch (Exception e) {
            log.error("发送邮件异常: ", e);
        }
    }

    public void send(List<String> to, List<String> cc, String subject, String content) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(from);
            if (to != null && to.size() > 0) {
                helper.setTo(to.toArray(new String[0]));
            }
            if (cc != null && cc.size() > 0) {
                helper.setCc(cc.toArray(new String[0]));
            }
            helper.setSubject(subject);
            helper.setText(content);



            emailSender.send(message);
        } catch (Exception e) {
            log.error("发送邮件异常: ", e);
        }
    }

    public void send(List<String> to, List<String> cc, String subject, String templateName, Map<String, Object> model) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            if (to != null && to.size() > 0) {
                helper.setTo(to.toArray(new String[0]));
            }
            if (cc != null && cc.size() > 0) {
                helper.setCc(cc.toArray(new String[0]));
            }
            helper.setSubject(subject);
            Template template = configurer.getConfiguration().getTemplate(templateName);
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            helper.setText(html, true);

            emailSender.send(message);
        } catch (Exception e) {
            log.error("发送邮件异常: {} ", e);
        }
    }

    public boolean send(List<String> to, List<String> cc, List<String> bcc, String subject, String templateName, Map<String, Object> model, Map<String, String> inLineMap) {
        try {
            log.info("调用邮件服务...");

            MimeMessage message = emailSender.createMimeMessage();
            message.setFrom(from);
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            if (to != null && to.size() > 0) {
                helper.setTo(to.toArray(new String[to.size()]));
            }

            if (cc != null && cc.size() > 0) {
                helper.setCc(cc.toArray(new String[cc.size()]));
            }

            if (bcc != null && bcc.size() > 0) {
                helper.setBcc(bcc.toArray(new String[bcc.size()]));
            }

            helper.setSubject(subject);

            Template template = configurer.getConfiguration().getTemplate(templateName);
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            helper.setText(html, true);

            if (!CollectionUtils.isEmpty(inLineMap)) {
                for (Map.Entry<String, String> entry : inLineMap.entrySet()) {
                    String key = entry.getKey();
                    String imgName = entry.getValue();
                    String location = "classpath:static/assets/images/" + imgName;

                    helper.addInline(key, new PathMatchingResourcePatternResolver().getResource(location));
                    // helper.addInline(key, new ClassPathResource(location));
                }
            }

            log.info("执行邮件服务发送...");
            emailSender.send(message);
            log.info("发送结束...");
            return true;
        } catch (Exception e) {
            log.error("发送邮件异常 error= [{}] , to = [{}] , cc = [{}] , subject = [{}] , template = [{}] , model = [{}]  inline = [{}] ", e.getMessage(),
                    to, cc, subject, templateName, model, inLineMap);
            log.error("发送邮件异常详情: {}", e);
            return false;
        }
    }

    public boolean send(List<String> to, List<String> cc, List<String> bcc, String subject, String templateName, Map<String, Object> model, Map<String, String> inLineMap, List<File> attachments) {
        try {
            log.info("调用邮件服务...");

            MimeMessage message = emailSender.createMimeMessage();
            message.setFrom(from);
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            if (to != null && to.size() > 0) {
                helper.setTo(to.toArray(new String[to.size()]));
            }

            if (cc != null && cc.size() > 0) {
                helper.setCc(cc.toArray(new String[cc.size()]));
            }

            if (bcc != null && bcc.size() > 0) {
                helper.setBcc(bcc.toArray(new String[bcc.size()]));
            }

            helper.setSubject(subject);

            Template template = configurer.getConfiguration().getTemplate(templateName);
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            helper.setText(html, true);

            if (!CollectionUtils.isEmpty(inLineMap)) {
                for (Map.Entry<String, String> entry : inLineMap.entrySet()) {
                    String key = entry.getKey();
                    String imgName = entry.getValue();
                    String location = "classpath:static/assets/images/" + imgName;

                    helper.addInline(key, new PathMatchingResourcePatternResolver().getResource(location));
                    // helper.addInline(key, new ClassPathResource(location));
                }
            }

            if (!CollectionUtils.isEmpty(attachments)) {
                for (File file : attachments) {
                    // 解决附件文件名乱码
                    helper.addAttachment(MimeUtility.encodeText(file.getName(),"UTF-8","B"), file);
                }
            }

            log.info("执行邮件服务发送...");
            emailSender.send(message);
            log.info("发送结束...");
            return true;
        } catch (Exception e) {
            log.error("发送邮件异常 error= [{}] , to = [{}] , cc = [{}] , subject = [{}] , template = [{}] , model = [{}]  inline = [{}] ", e.getMessage(),
                    to, cc, subject, templateName, model, inLineMap);
            log.error("发送邮件异常详情: {}", e);
            return false;
        }
    }

    public boolean send(String sendFrom, List<String> to, List<String> cc, List<String> bcc, String subject, String templateName, Map<String, Object> model, Map<String, String> inLineMap) {
        if (StringUtils.isEmpty(sendFrom)) {
            sendFrom = from;
        }
        try {
            log.info("调用邮件服务...");

            MimeMessage message = emailSender.createMimeMessage();
            message.setFrom(sendFrom);
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            if (to != null && to.size() > 0) {
                helper.setTo(to.toArray(new String[to.size()]));
            }

            if (cc != null && cc.size() > 0) {
                helper.setCc(cc.toArray(new String[cc.size()]));
            }

            if (bcc != null && bcc.size() > 0) {
                helper.setBcc(bcc.toArray(new String[bcc.size()]));
            }

            helper.setSubject(subject);

            Template template = configurer.getConfiguration().getTemplate(templateName);
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            helper.setText(html, true);

            if (!CollectionUtils.isEmpty(inLineMap)) {
                for (Map.Entry<String, String> entry : inLineMap.entrySet()) {
                    String key = entry.getKey();
                    String imgName = entry.getValue();
                    String location = "classpath:static/assets/images/" + imgName;

                    helper.addInline(key, new PathMatchingResourcePatternResolver().getResource(location));
                    // helper.addInline(key, new ClassPathResource(location));
                }
            }

            log.info("执行邮件服务发送...");
            emailSender.send(message);
            log.info("发送结束...");
            return true;
        } catch (Exception e) {
            log.error("发送邮件异常 error= [{}] , to = [{}] , cc = [{}] , subject = [{}] , template = [{}] , model = [{}]  inline = [{}] ", e.getMessage(),
                    to, cc, subject, templateName, model, inLineMap);
            log.error("发送邮件异常详情: {}", e);
            return false;
        }
    }

    public boolean send(String sendFrom, List<String> to, List<String> cc, List<String> bcc, String subject, String templateName, Map<String, Object> model, Map<String, String> inLineMap, List<File> attachments) {
        if (StringUtils.isEmpty(sendFrom)) {
            sendFrom = from;
        }
        try {
            log.info("调用邮件服务...");

            MimeMessage message = emailSender.createMimeMessage();
            message.setFrom(sendFrom);
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            if (to != null && to.size() > 0) {
                helper.setTo(to.toArray(new String[to.size()]));
            }

            if (cc != null && cc.size() > 0) {
                helper.setCc(cc.toArray(new String[cc.size()]));
            }

            if (bcc != null && bcc.size() > 0) {
                helper.setBcc(bcc.toArray(new String[bcc.size()]));
            }

            helper.setSubject(subject);

            Template template = configurer.getConfiguration().getTemplate(templateName);
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            helper.setText(html, true);

            if (!CollectionUtils.isEmpty(inLineMap)) {
                for (Map.Entry<String, String> entry : inLineMap.entrySet()) {
                    String key = entry.getKey();
                    String imgName = entry.getValue();
                    String location = "classpath:static/assets/images/" + imgName;

                    helper.addInline(key, new PathMatchingResourcePatternResolver().getResource(location));
                    // helper.addInline(key, new ClassPathResource(location));
                }
            }

            if (!CollectionUtils.isEmpty(attachments)) {
                for (File file : attachments) {
                    // 解决附件文件名乱码
                    helper.addAttachment(MimeUtility.encodeText(file.getName(),"UTF-8","B"), file);
                }
            }

            log.info("执行邮件服务发送...");
            emailSender.send(message);
            log.info("发送结束...");
            return true;
        } catch (Exception e) {
            log.error("发送邮件异常 error= [{}] , to = [{}] , cc = [{}] , subject = [{}] , template = [{}] , model = [{}]  inline = [{}] ", e.getMessage(),
                    to, cc, subject, templateName, model, inLineMap);
            log.error("发送邮件异常详情: {}", e);
            return false;
        }
    }

}
