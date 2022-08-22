package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    private static final String REPLACEMENT = "***";

    private TireNode rootNode = new TireNode();



    @PostConstruct
    public void init(){
        try (

                InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        ) {
            String keyword;
            while ((keyword = reader.readLine()) != null){
                this.addKeyword(keyword);
            }

        } catch (IOException e){
            logger.error("加载敏感词文件失败"+e);
        }
        
    }

    public void addKeyword(String keyword){
        TireNode tempNode = rootNode;
        for (int i =0;i<keyword.length();i++){
            char c = keyword.charAt(i);
            TireNode subNode = tempNode.getSubNode(c);
            if(subNode == null){
                subNode = new TireNode();
                tempNode.addSubNode(c,subNode);
            }
            tempNode = subNode;

            if(i == keyword.length() -1){
                tempNode.isKeywordEnd = true;
            }
        }
    }

    public String filter(String text) {

        if (StringUtils.isBlank(text)) {
            return null;
        }

        TireNode tempNode = rootNode;
        int begin = 0;
        int position = 0;
        StringBuilder stringBuilder = new StringBuilder();

        while (begin < text.length()) {
            if (position < text.length()) {
                char c = text.charAt(position);
                if (isSymbol(c)) {
                    if (tempNode == rootNode) {
                        begin++;
                        stringBuilder.append(c);
                    }
                    position++;
                    continue;
                }
                tempNode = tempNode.getSubNode(c);
                if (tempNode == null) {
                    stringBuilder.append(text.charAt(begin));
                    position = ++begin;
                    tempNode = rootNode;
                } else if (tempNode.isKeywordEnd()) {
                    stringBuilder.append(REPLACEMENT);
                    begin = ++position;
                } else {
                    position++;
                }
            }
            else {
                stringBuilder.append(text.charAt(begin));
                position = ++begin;
                tempNode = rootNode;
            }
        }

        return stringBuilder.toString();
    }

    private boolean isSymbol(Character c){
        return !CharUtils.isAsciiAlphanumeric(c) && (c< 0x2E80 || c>0x9FFF);
    }

    private class TireNode{

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        private boolean isKeywordEnd = false;

        private Map<Character,TireNode> subNodes = new HashMap<>();

        public void addSubNode(Character c,TireNode tireNode){
                subNodes.put(c,tireNode);
        }

        public TireNode getSubNode(Character c){
            return subNodes.get(c);
        }

    }

}
