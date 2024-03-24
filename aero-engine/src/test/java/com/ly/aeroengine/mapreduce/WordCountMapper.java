package com.ly.aeroengine.mapreduce;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * @Author ly
 * @Date 2024 03 22 02 10
 **/
public class WordCountMapper extends Mapper<LongWritable, Text,Text,LongWritable> {
    @Override
    protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, LongWritable>.Context context) throws IOException, InterruptedException {
        String[] split = value.toString().split(",");
        for (String word : split) {
            context.write(new Text(word),new LongWritable(1));
        }
    }
}
