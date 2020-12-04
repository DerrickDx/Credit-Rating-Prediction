import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class creditrating {

    public static void main(String[] args) {

        String trainSetName;

        String testSetName;

        int minleaf;

        BufferedReader br = null;


        try {

            trainSetName = args[0];
            testSetName = args[1];
            minleaf = Integer.parseInt(args[2]);

            File file = new File(trainSetName);
            br = new BufferedReader(new FileReader(file));

            String textLine;

            List<String> train_text_list = new ArrayList<>();
            br.readLine();
            while ((textLine = br.readLine())!=null) {

                train_text_list.add(textLine);
            }

            List<Double[]> train_data_list = new ArrayList<>();
            for (int i = 0; i < train_text_list.size(); i++) {
                String[] ts = train_text_list.get(i).trim().split(" +");
                Double[] td = new Double[6];
                for (int j = 0; j < ts.length -1; j++) {
                    td[j] = Double.parseDouble(ts[j]);
                }

                td[5] = Double.parseDouble(ts[5].replaceAll("AAA","0").replaceAll("AA","1")
                        .replaceAll("A","2").replaceAll("BBB","3")
                        .replaceAll("BB","4").replaceAll("B","5")
                        .replaceAll("CCC","6"));

                train_data_list.add(td);
            }


//
//            for (int i = 0; i < train_data_list.get(5).length; i++) {
//                System.out.println("s[j]:"+train_data_list.get(5)[i]);
//            }
//            System.out.println("train_text_list rating:"+train_text_list.get(99).substring(train_text_list.get(99).lastIndexOf(" ")+1));
//            System.out.println("train_text_list rating:"+train_text_list.get(98).substring(train_text_list.get(98).lastIndexOf(" ")+1));
//            System.out.println("train_text_list rating:"+train_text_list.get(95).substring(train_text_list.get(95).lastIndexOf(" ")+1));

//            getNumbers(train_data_list);
//            System.out.println(getUniqueMode(train_data_list));

//            double infoContent = computeInfoContent(train_data_list);
//

//            System.out.println(train_data_list.get(train_data_list.size()-1)[0]+","+
//                    train_data_list.get(train_data_list.size()-1)[1]+","+
//                    train_data_list.get(train_data_list.size()-1)[2]+","+
//                    train_data_list.get(train_data_list.size()-1)[3]+","+
//                    train_data_list.get(train_data_list.size()-1)[4]+","+
//                    train_data_list.get(train_data_list.size()-1)[5]);




            file = new File(testSetName);
            br = new BufferedReader(new FileReader(file));
            List<String> test_text_list = new ArrayList<>();
            br.readLine();

            while ((textLine = br.readLine())!=null) {
                test_text_list.add(textLine);
            }
            List<Double[]> test_data_list = new ArrayList<>();

            for (int i = 0; i < test_text_list.size(); i++) {
                String[] ts = test_text_list.get(i).trim().split(" +");
                Double[] td = new Double[5];
                for (int j = 0; j < ts.length; j++) {
                    td[j] = Double.parseDouble(ts[j]);
                }
                test_data_list.add(td);
            }
            br.close();

            Node dtl = DTL(train_data_list, train_text_list, minleaf);

            predict(dtl, test_data_list);

        } catch (Exception e){
            e.printStackTrace();
        }
    }



    public static Node DTL(List<Double[]> dataList, List<String> textList, int minLeaf) {

        Node node = new Node();
        // if(N≤minleaf)or(yi=yj foralli,j)or(xi=xj foralli,j)then
        boolean sameData = true;
        for (int i = 0; i < textList.size() - 1; i++) {
            String text1 = textList.get(i).trim().replaceAll(" +",",");
            String text2 = textList.get(i+1).trim().replaceAll(" +",",");

            if (!(text1.substring(0,text1.length()-2)).equals(text2.substring(0,text2.length()-2)) ||
                    !(text1.substring(text1.length()-1)).equals(text2.substring(text2.length()-1))) {
                sameData = false;
                break;
            }
        }


        if ( sameData || dataList.size() <= minLeaf ) {

            node.label = getUniqueMode(dataList);
            return node;
        }

        // [attr,splitval] ← choose-split(data)
        String bestInfo = chooseBest(dataList);
        String[] info = bestInfo.split(",");
        String attr = info[0];
        Integer attrInt = Integer.parseInt(attr);
        Double splitValue = Double.parseDouble(info[1]);


        node.attr = attrInt;
        node.splitValue = splitValue;

        List<Double[]> leftList = new ArrayList<>();
        List<Double[]> rightList = new ArrayList<>();

        List<String> leftTextList = new ArrayList<>();
        List<String> rightTextList = new ArrayList<>();

        for (int i = 0; i < dataList.size(); i++) {

            if (dataList.get(i)[attrInt] <= splitValue) {
                leftList.add(dataList.get(i));
                leftTextList.add(textList.get(i));
            } else {
                rightList.add(dataList.get(i));
                rightTextList.add(textList.get(i));
            }
        }


        node.leftNode = DTL(leftList, leftTextList, minLeaf);
        node.rightNode = DTL(rightList, rightTextList, minLeaf);

        return node;
    }



    public static String getUniqueMode(List<Double[]> dataList) {

        String rating;
        int[] record = getNumbers(dataList);
        int max = -1;
        int count = 0;
        String pos = "";
        for (int i = 0; i < record.length; i++) {
            if (record[i] > max ) {
                max = record[i];
                count = 1;
                pos = i+"";
            } else if(record[i] == max){
                count++;
            }

        }
        if (count == 1)  {
            pos = pos.replaceAll("0","AAA").replaceAll("1","AA")
                        .replaceAll("2","A").replaceAll("3","BBB")
                        .replaceAll("4","BB").replaceAll("5","B")
                        .replaceAll("6","CCC");

            return pos;
        }
        else  {
            return "unknown";
        }
    }


    public static int[] getNumbers(List<Double[]> dataList) {
        int rating;
        int[] record = new int[7];
        for (int i = 0; i < dataList.size(); i++) {

            rating = dataList.get(i)[5].intValue();

            switch(rating)
            {
                case 0:
                    record[0] = record[0]+1;
                    break;
                case 1:
                    record[1] = record[1]+1;
                    break;
                case 2:
                    record[2] = record[2]+1;
                    break;
                case 3:
                    record[3] = record[3]+1;
                    break;
                case 4:
                    record[4] = record[4]+1;
                    break;
                case 5:
                    record[5] = record[5]+1;
                    break;
                case 6:
                    record[6] = record[6]+1;
                    break;
                default:
                    System.out.println("no match");
            }
        }


        return record;
    }

    public static double computeInfoContent(List<Double[]> dataList) {
        int[] record = getNumbers(dataList);
        double res = 0;
        double naN = Double.NaN;
        for (int i = 0; i < record.length; i++) {
            double logValue = 0.000000000000;
            double probability = Double.parseDouble(record[i]+"")/dataList.size();

            if (probability != 0) {
                logValue = probability * (Math.log(probability)/Math.log(2));
//                if (logValue+"" == NaN) logValue = 0;
            }
//            System.out.println("record[i]:"+record[i]+", probability:"+probability+", logValue:"+logValue);
            res = res + logValue;
        }
        res = -res;
        if (res == -0) res = 0;
//        System.out.println("res:"+res);

        return res;
    }

    public static String chooseBest(List<Double[]> dataList) {

        String bestInfo = "";
        double bestGain = -1;


        double rootInfoContent = computeInfoContent(dataList);


        for (int attr = 0; attr < 5; attr++) {

            // Sort the array x1[attr], x2[attr], . . . , xN [attr]
            int attribute = attr;
            Comparator<Double[]> comparator = ((o1, o2) -> o1[attribute].compareTo(o2[attribute]));


            Collections.sort(dataList, comparator);

            for (int j = 0; j < dataList.size() - 1; j++) {

                // splitval ← 0.5(xi[attr] + xi+1[attr])
                double splitValue = 0.5 * (dataList.get(j)[attr] + dataList.get(j+1)[attr]);

                List<Double[]> leftList = new ArrayList<>();
                List<Double[]> rightList = new ArrayList<>();

                for (int k = 0; k < dataList.size(); k++) {

                    if (dataList.get(k)[attr] <= splitValue) {
                        leftList.add(dataList.get(k));
//
                    } else {
                        rightList.add(dataList.get(k));
                    }
                }

                double leftInfoContent = computeInfoContent(leftList);
                double rightInfoContent = computeInfoContent(rightList);


                double remainder = leftInfoContent * (Double.parseDouble(leftList.size()+"")/Double.parseDouble(dataList.size()+""))
                        + rightInfoContent * (Double.parseDouble(rightList.size()+"")/Double.parseDouble(dataList.size()+""));
                double gain = rootInfoContent - remainder;

                // bestattr ← attr and bestsplitval ← splitval
                if (gain > bestGain) {
                    bestGain = gain;
                    bestInfo = attr+","+splitValue;
                }
            }
        }


        return bestInfo;
    }




    public static void predict(Node root, List<Double[]> dataList) {


        for (int i = 0; i < dataList.size(); i++) {
            Double[] data = dataList.get(i);
            Node rootNode = root;

            while (rootNode.label == null) {
                if (data[rootNode.attr] <= rootNode.splitValue) {
                    rootNode = rootNode.leftNode;
                }
                else {
                    rootNode = rootNode.rightNode;
                }

            }
            System.out.println(" " + rootNode.label);
        }
    }


}

