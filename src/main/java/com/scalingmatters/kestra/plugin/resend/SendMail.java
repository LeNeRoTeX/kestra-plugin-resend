package com.scalingmatters.kestra.plugin.resend;

import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import io.kestra.core.models.annotations.Plugin;
import io.kestra.core.models.property.Property;
import io.kestra.core.models.tasks.RunnableTask;
import io.kestra.core.models.tasks.Task;
import io.kestra.core.runners.RunContext;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@ToString
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Schema(
    title = "Send an email using Resend",
    description = "See the [Resend documentation](https://resend.com/docs) for more information."
)
@Plugin(
    examples = {
        @io.kestra.core.models.annotations.Example(
            title = "Send a simple email",
            code = { "apiKey: \"<yourApiKey>\"" +
                "from: \"kestra@example.none\"" +
                "to: \"user@example.none\"" +
                "subject: \"This is a test mail\"" +
                "html: \"<h1>This is a test mail</h1>\""
            }
        )
    }
)
public class SendMail extends Task implements RunnableTask<SendMail.Output> {

    @Schema(
        title = "The API key to use to connect to Resend",
        description = "You can find your API key in your Resend dashboard"
    )
    private Property<String> apiKey;

    @Schema(
        title = "The email address to send the email from",
        description = "You can only send emails from verified email addresses"
    )
    private Property<String> from;

    @Schema(
        title = "The email address to send the email to"
    )
    private Property<String> to;

    @Schema(
        title = "The subject of the email"
    )
    private Property<String> subject;

    @Schema(
        title = "The html content of the email"
    )
    private Property<String> html;

    @Override
    public SendMail.Output run(RunContext runContext) throws Exception {
        String apiKey = runContext.render(this.apiKey).as(String.class).orElseThrow(() -> new ResendException("apiKey is required"));
        String from = runContext.render(this.from).as(String.class).orElseThrow(() -> new ResendException("from is required"));
        String to = runContext.render(this.to).as(String.class).orElseThrow(() -> new ResendException("to is required"));
        String subject = runContext.render(this.subject).as(String.class).orElseThrow(() -> new ResendException("subject is required"));
        String html = runContext.render(this.html).as(String.class).orElseThrow(() -> new ResendException("html is required"));

        Resend resend = new Resend(apiKey);

        CreateEmailOptions options = CreateEmailOptions.builder()
            .from(from)
            .to(to)
            .subject(subject)
            .html(html)
            .build();

        CreateEmailResponse response = resend.emails().send(options);

        return Output.builder()
            .id(response.getId())
            .build();
    }

    @Builder
    @Getter
    public static class Output implements io.kestra.core.models.tasks.Output {
        @Schema(
            title = "The id of the email"
        )
        private final String id;
    }
}
