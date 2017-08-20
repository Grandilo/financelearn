package com.grandilo.financelearn.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.grandilo.financelearn.R;
import com.grandilo.financelearn.utils.FinanceLearningConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * @author Ugo
 */

public class MainTestQuestionAndAnswersAdapter extends PagerAdapter {

    private List<JSONObject> mainTestQuestions;
    private LayoutInflater layoutInflater;

    public MainTestQuestionAndAnswersAdapter(Context context, List<JSONObject> mainTestQuestions) {
        this.mainTestQuestions = mainTestQuestions;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mainTestQuestions != null ? mainTestQuestions.size() : 0;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        JSONObject mainTestQuestion = mainTestQuestions.get(position);
        View convertView = layoutInflater.inflate(R.layout.question_and_answer_item, collection, false);
        setupViews(convertView, mainTestQuestion);
        collection.addView(convertView);
        return convertView;
    }

    private void setupViews(View parentView, JSONObject mainTestItem) {

        TextView questionView = parentView.findViewById(R.id.question);
        ViewFlipper optionsFlipper = parentView.findViewById(R.id.options_flipper);

        //Setup Questions
        String question = mainTestItem.optString(FinanceLearningConstants.QUESTION);
        questionView.setText(question);

        //Setup Options
        JSONArray optionsArray = mainTestItem.optJSONArray(FinanceLearningConstants.OPTIONS);
        String answer = mainTestItem.optString(FinanceLearningConstants.ANSWER);
        String courseId = mainTestItem.optString(FinanceLearningConstants.COURSE_ID);

        String[] answers = answer.split("\\*");

        if (answers.length > 1) {
            setupMultiChoiceQuestions(parentView, optionsFlipper, question, courseId, answers, optionsArray);
        } else {
            setupSingleChoiceQuestions(parentView, optionsFlipper, question, answer, courseId, optionsArray);
        }

    }

    private void setupMultiChoiceQuestions(View parentView, ViewFlipper optionsFlipper, final String question, final String courseId, final String[] answers, JSONArray options) {
        optionsFlipper.setDisplayedChild(1);
        RadioGroup multiChoiceRadioGroup = parentView.findViewById(R.id.multi_select_radio_group);
        for (int i = 0; i < options.length(); i++) {
            final String optionName = options.optString(i);
            CheckBox radioButton = (CheckBox) multiChoiceRadioGroup.getChildAt(i);
            if (radioButton != null) {
                radioButton.setText(optionName);
                radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                        JSONObject courseReference = (JSONObject) FinanceLearningConstants.mainTestResult.get(courseId);

                        if (courseReference != null) {
                            JSONObject questionReference = courseReference.optJSONObject(String.valueOf(question.trim().hashCode()));
                            if (questionReference != null) {
                                courseReference.remove(String.valueOf(question.trim().hashCode()));
                            }
                        }

                        if (checked) {
                            try {
                                List<String> answersList = Arrays.asList(answers);
                                if (answersList.contains(optionName.trim())) {
                                    JSONObject selectedOption = getSelectedOption(question, optionName);
                                    if (courseReference != null) {
                                        injectSelectedOption(selectedOption, courseReference, question, courseId);
                                    } else {
                                        JSONObject newCourseReference = new JSONObject();
                                        injectSelectedOption(selectedOption, newCourseReference, question, courseId);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        FinanceLearningConstants.selectedAnOption.put(question, true);

                    }

                });

            }

        }

    }

    private void setupSingleChoiceQuestions(View parentView, ViewFlipper optionsFlipper, final String question, final String answer, final String courseId, JSONArray options) {

        optionsFlipper.setDisplayedChild(0);
        RadioGroup singleSelectRadioGroup = parentView.findViewById(R.id.single_select_radio_group);

        for (int i = 0; i < options.length(); i++) {
            final String optionName = options.optString(i);
            RadioButton radioButton = (RadioButton) singleSelectRadioGroup.getChildAt(i);
            if (radioButton != null) {
                radioButton.setText(optionName);
                radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                        JSONObject courseReference = (JSONObject) FinanceLearningConstants.mainTestResult.get(courseId);

                        if (courseReference != null) {
                            JSONObject questionReference = courseReference.optJSONObject(String.valueOf(question.trim().hashCode()));
                            if (questionReference != null) {
                                courseReference.remove(String.valueOf(question.trim().hashCode()));
                            }
                        }

                        if (checked) {
                            try {
                                if (answer.contains(optionName.trim())) {
                                    JSONObject selectedOption = getSelectedOption(question, optionName);
                                    if (courseReference != null) {
                                        injectSelectedOption(selectedOption, courseReference, question, courseId);
                                    } else {
                                        JSONObject newCourseReference = new JSONObject();
                                        injectSelectedOption(selectedOption, newCourseReference, question, courseId);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        FinanceLearningConstants.selectedAnOption.put(question, true);

                    }

                });

            }

        }

    }

    private void injectSelectedOption(JSONObject selectedOption, JSONObject courseReference, String question, String courseId) throws JSONException {
        courseReference.put(String.valueOf(question.trim().hashCode()), selectedOption);
        FinanceLearningConstants.mainTestResult.put(courseId, courseReference);
    }

    @NonNull
    private JSONObject getSelectedOption(String question, String answer) {
        JSONObject selectedOptionObject = new JSONObject();
        try {
            selectedOptionObject.put(FinanceLearningConstants.QUESTION, question);
            selectedOptionObject.put(FinanceLearningConstants.PICKED_OPTION, answer);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return selectedOptionObject;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}
