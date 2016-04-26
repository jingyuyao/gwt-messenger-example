package com.jingyu.example.client.ui;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.jingyu.example.shared.Message;

/**
 * Created by jingyu on 4/25/16.
 */
public class MessageCell extends AbstractCell<Message> {
    /**
     * Cannot be private because GWT's deferred binding
     */
    interface Templates extends SafeHtmlTemplates{
        @SafeHtmlTemplates.Template("<div><p><b>{0}</b>{1}</p></div>")
        SafeHtml cell(SafeHtml date, SafeHtml content);
    }

    private static Templates templates = GWT.create(Templates.class);
    private static DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT);

    @Override
    public void render(Context context, Message value, SafeHtmlBuilder sb) {
        if (value == null) {
            return;
        }
        SafeHtml date = SafeHtmlUtils.fromTrustedString(DATE_FORMAT.format(value.created));
        SafeHtml content = SafeHtmlUtils.fromString(value.content);
        SafeHtml rendered = templates.cell(date, content);
        sb.append(rendered);
    }
}
