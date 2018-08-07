package com.application.tippzi.Adapters;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

public class CategorySpinner<T> {
    private static final String TAG = CategorySpinner.class.getSimpleName();

    /**
     * Used to handle the spinner events.
     *
     * @param <T> Type of the data used by the spinner
     */
    public interface Callback<T> {
        /**
         * When a spinner item has been selected.
         *
         * @param position Position selected
         * @param itemAtPosition Item selected
         */
        void onItemSelected(int position, T itemAtPosition);
    }

    private final Spinner spinner;
    private final CategorySpinnerAdapter<T> adapter;
    private final Callback<T> callback;

    public CategorySpinner(Spinner spinner, CategorySpinnerAdapter<T> adapter, Callback<T> callback) {
        this.spinner = spinner;
        this.adapter = adapter;
        this.callback = callback;
    }

    /**
     * Initializes the hint spinner.
     *
     * By default, the hint is selected when calling this method.
     */
    public void init() {
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "position selected: " + position);
                if (CategorySpinner.this.callback == null) {
                    throw new IllegalStateException("callback cannot be null");
                }
                if (!isHintPosition(position)) {
                    Object item = CategorySpinner.this.spinner.getItemAtPosition(position);
                    CategorySpinner.this.callback.onItemSelected(position, (T) item);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "Nothing selected");
            }
        });
        selectHint();
    }

    private boolean isHintPosition(int position) {
        return position == adapter.getHintPosition();
    }

    /**
     * Selects the hint element.
     */
    public void selectHint() {
        spinner.setSelection(adapter.getHintPosition());
    }
}
