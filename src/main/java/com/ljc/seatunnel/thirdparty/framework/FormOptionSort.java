package com.ljc.seatunnel.thirdparty.framework;

import com.ljc.seatunnel.dynamicforms.AbstractFormOption;
import com.ljc.seatunnel.dynamicforms.Constants;
import com.ljc.seatunnel.dynamicforms.FormStructure;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FormOptionSort {
    /** Sort form structure. */
    public static FormStructure sortFormStructure(@NonNull FormStructure formStructure) {
        List<AbstractFormOption> newFormOptions = new ArrayList<>();
        List<AbstractFormOption> formOptions = formStructure.getForms();
        formOptions.forEach(
                currFormOption -> {
                    if (currFormOption.getShow() != null && currFormOption.getShow().size() > 0) {
                        return;
                    }
                    addShowOptionAfter(currFormOption, formOptions, newFormOptions);
                });

        return FormStructure.builder()
                .name(formStructure.getName())
                .withLocale(formStructure.getLocales())
                .addFormOption(newFormOptions.toArray(new AbstractFormOption[0]))
                .build();
    }

    public static void addShowOptionAfter(
            @NonNull AbstractFormOption currFormOption,
            @NonNull List<AbstractFormOption> allFormOptions,
            @NonNull List<AbstractFormOption> newFormOptions) {
        if (newFormOptions.contains(currFormOption)) {
            return;
        }

        newFormOptions.add(currFormOption);

        List<AbstractFormOption> showOptions =
                allFormOptions.stream()
                        .filter(
                                nextOption -> {
                                    return nextOption.getShow() != null
                                            && nextOption.getShow().size() > 0
                                            && nextOption
                                                    .getShow()
                                                    .get(Constants.SHOW_FIELD)
                                                    .toString()
                                                    .equals(currFormOption.getField());
                                })
                        .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(showOptions)) {
            return;
        }

        for (AbstractFormOption showOption : showOptions) {
            addShowOptionAfter(showOption, allFormOptions, newFormOptions);
        }
    }
}
