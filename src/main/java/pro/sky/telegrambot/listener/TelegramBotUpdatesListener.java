package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.aspectj.weaver.patterns.ISignaturePattern.PATTERN;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {
public static final Pattern PATTERN=Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;
    @Autowired
    private NotificationTaskRepository repository;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            String text = update.message().text();
            Long chatId=update.message().chat().id();
            Matcher matcher = PATTERN.matcher(text);
            if ("/start".equalsIgnoreCase(text)){
                telegramBot.execute(new SendMessage(chatId, "Привет!"));
            }else if (matcher.matches()){
               String time= matcher.group(1);
                LocalDateTime execDate = LocalDateTime
                        .parse(time, FORMATTER);
                NotificationTask task = new NotificationTask();
                task.setChatId(chatId);
                task.setText(matcher.group(3));
                task.setExecDate(execDate);
                repository.save(task);
                }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
