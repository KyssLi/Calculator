package com.calculator.calculator_sak;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.boris.expr.Expr;
import org.boris.expr.ExprEvaluatable;
import org.boris.expr.parser.ExprParser;
import org.boris.expr.util.Exprs;
import org.boris.expr.util.SimpleEvaluationContext;

public class MainActivity extends AppCompatActivity {
    private EditText editText; //这是文本显示框
    private String strShowWindow = "0"; //始终显示的文本
    private boolean boolPoint = true; //用于判断是否可以使用"."
    private boolean boolButton = true; //用于判断是否可以使用"+-*/="
    private int right = 0, left = 0; //分别用于统计"("和")"的使用个数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText)findViewById(R.id.show_window);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sak_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_settings:
                show_message("build 10404\nby Daming Li\nCopyright(C) 2015");
                return true;
            case R.id.menu_minesweeper:
                show_message("Developing...");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void show_message(String str) {
        AlertDialog message = new AlertDialog.Builder(this).create();
        message.setTitle("About Me");
        message.setMessage(str);
        message.setButton(AlertDialog.BUTTON_NEUTRAL, "Get", listener);
        message.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
            // 创建退出对话框
            AlertDialog isExit = new AlertDialog.Builder(this).create();
            // 设置对话框标题
            isExit.setTitle("Tips!");
            // 设置对话框消息
            isExit.setMessage("Do you want to exit?");
            // 添加选择按钮并注册监听
            isExit.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", listener);
            isExit.setButton(AlertDialog.BUTTON_NEGATIVE, "No", listener);
            // 显示对话框
            isExit.show();
        }
        return false;
    }

    //监听对话框里面的button点击事件
    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
    {
        public void onClick(DialogInterface dialog, int which)
        {
            switch (which)
            {
                case AlertDialog.BUTTON_POSITIVE: //"Yes"按钮退出程序
                    finish();
                    break;
                case AlertDialog.BUTTON_NEGATIVE: //"No"按钮取消对话框
                    break;
                case AlertDialog.BUTTON_NEUTRAL: //"Know!"按钮取消对话框
                    break;
                default:
                    break;
            }
        }
    };

    //处理数字按钮
    public void func_num(View v) {
        String strTmp = ((Button)v).getText().toString();
        if(strShowWindow.equals("0")) {
            strShowWindow = strTmp;
        }
        else {
            strShowWindow += strTmp;
        }
        boolButton = true;
        showWindowText(strShowWindow);
    }

    //处理非数字按钮
    public void func_symbol(View v) {
        String strTmp = ((Button)v).getText().toString();
        switch (strTmp) {
            case ".":
                if (boolPoint) {
                    if (isSymbol()) {
                        strShowWindow += "0";
                    }
                    strShowWindow += strTmp;
                    boolPoint = false;
                }
                break;
            case "(":
                if (strShowWindow.equals("0")) {
                    strShowWindow = "(";
                }
                else {
                    strShowWindow += "(";
                }
                ++left;
                break;
            case ")":
                if (left > right) {
                    strShowWindow += ")";
                    ++right;
                }
                break;
            case "C":
                if (strShowWindow.equals("0.")) {
                    strShowWindow = "";
                    boolPoint = true;
                    boolButton = true;
                    break;
                }
                if (strShowWindow.length() != 1) {
                    dealWithBracket();
                    strShowWindow = strShowWindow.substring(0, strShowWindow.length() - 1);
                }
                else {
                    dealWithBracket();
                    strShowWindow = "";
                    boolPoint = true;
                    boolButton = true;
                    break;
                }
                if (!isSymbol()) {
                    boolButton = true;
                }
                if (!hasPoint()) {
                    boolPoint = true;
                }
                break;
            case "+":
                if (boolButton) {
                    strShowWindow += "+";
                    boolButton = false;
                    boolPoint = true;
                }
                break;
            case "-":
                if (boolButton) {
                    strShowWindow += "-";
                    boolButton = false;
                    boolPoint = true;
                }
                break;
            case "*":
                if (boolButton) {
                    strShowWindow += "*";
                    boolButton = false;
                    boolPoint = true;
                }
                break;
            case "/":
                if (boolButton) {
                    strShowWindow += "/";
                    boolButton = false;
                    boolPoint = true;
                }
                break;
            default:
                break;
        }
        showWindowText(strShowWindow);
    }

    private void dealWithBracket() {
        if (strShowWindow.charAt(strShowWindow.length() - 1) == '(') {
            --left;
        }
        else if (strShowWindow.charAt(strShowWindow.length() - 1) == ')') {
            --right;
        }
    }

    public void func_equal(View v) {
        strShowWindow = calculate(strShowWindow);
        showWindowText(strShowWindow);
        strShowWindow = "0";
        right = 0;
        left = 0;
        boolButton = true;
        boolPoint = true;
    }

    //判断当前最近输入的一个数是否有"."
    private boolean hasPoint() {
        if (isSymbol()) {
            return false;
        }
        else if (isPoint()) {
            return true;
        }
        else {
            int posPoint = -1;
            for(int i = strShowWindow.length() - 2; i >= 0; --i) {
                if (isPoint(i)) {
                    posPoint = i;
                }
                else if (isSymbol(i)) {
                    if (posPoint == -1) {
                        return false;
                    }
                    else if (posPoint >= i) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    //判断最后个字符是不是"."
    private boolean isPoint() {
        return isPoint(strShowWindow.length() - 1);
    }

    private boolean isPoint(int pos) {
        return strShowWindow.charAt(pos) == '.';
    }

    //判断最后个字符是不是"+-*/"
    private boolean isSymbol() {
        return isSymbol(strShowWindow.length() - 1);
    }

    private boolean isSymbol(int pos) {
        if(strShowWindow.charAt(pos) == '+') {
            return true;
        }
        else if(strShowWindow.charAt(pos) == '-') {
            return true;
        }
        else if(strShowWindow.charAt(pos) == '*') {
            return true;
        }
        else if(strShowWindow.charAt(pos) == '/') {
            return true;
        }
        return false;
    }

    private void showWindowText(String str) {
        if (str.length() < 18) {
            editText.setText(str);
        }
        else {
            String strTmp = str.substring(str.length() - 17);
            editText.setText(strTmp);
        }
        if (str.isEmpty()) {
            strShowWindow = "0";
        }
    }

    private String calculate(String str) {
        SimpleEvaluationContext context = new SimpleEvaluationContext();
        if (!str.equals("0")) {
            try {
                Expr e = ExprParser.parse(str);
                Exprs.toUpperCase(e);
                if(e instanceof ExprEvaluatable) {
                    e = ((ExprEvaluatable)e).evaluate(context);
                }
                return e.toString();
            } catch(Exception e) {
                return e.toString();
            }
        }
        return str;
    }

}
