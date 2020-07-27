import getopt
import sys
import pandas as pd
import math


class Node:

    def __init__(self, attr, split_value, label, left_node, right_node):
        self.attr = attr
        self.split_value = split_value
        self.label = label
        self.left_node = left_node
        self.right_node = right_node


def decision_tree_learning(data_list, min_leaf):
    same_data = True
    for i in range(0, len(data_list) - 1):
        list1 = data_list[i]
        list2 = data_list[i + 1]
        if (list1[0] != list2[0] or list1[1] != list2[1] or list1[2] != list2[2] or list1[3] != list2[3]
            or list1[4] != list2[4]) or (list1[5] != list2[5]):
            same_data = False
            break

    if same_data or len(data_list) <= min_leaf:
        result = get_numbers(data_list)
        highest_frequency = result[result.values == result[0]].index
        label = highest_frequency[0]

        if len(highest_frequency) > 1:
            label = 'unknown'

        return Node(None, None, label, None, None)

    best_info = choose_best(data_list)

    left_list = []
    right_list = []
    for j in range(len(data_list)):
        if float(data_list[j][best_info[0]]) <= best_info[1]:
            left_list.append(data_list[j])
        else:
            right_list.append(data_list[j])

    left_node = decision_tree_learning(left_list, min_leaf)
    right_node = decision_tree_learning(right_list, min_leaf)

    return Node(best_info[0], best_info[1], None, left_node, right_node)


def get_numbers(data_list):
    record = []
    for i, val in enumerate(data_list):
        rating = data_list[i][5]
        record.append(rating)

    result = pd.value_counts(record)
    return result


def choose_best(data_list):
    best_gain = -1
    root_info_content = compute_info_content(data_list)
    best_info = []
    for i in range(5):
        sorted_data_list = sorted(filter(lambda x: float(x[i]) < 0, data_list), key=(lambda x: x[i]), reverse=True) \
                           + sorted(filter(lambda x: float(x[i]) > 0, data_list), key=(lambda x: x[i]))

        for j in range(0, len(sorted_data_list) - 1):
            split_value = 0.5 * (float(sorted_data_list[j][i]) + float(sorted_data_list[j + 1][i]))
            left_list = []
            right_list = []

            for k in range(len(sorted_data_list)):
                if float(sorted_data_list[k][i]) <= split_value:
                    left_list.append(sorted_data_list[k])
                else:
                    right_list.append(sorted_data_list[k])

            left_info_content = compute_info_content(left_list)
            right_info_content = compute_info_content(right_list)

            remainder = left_info_content * (len(left_list) / len(sorted_data_list)) \
                        + right_info_content * (len(right_list) / len(sorted_data_list))
            gain = root_info_content - remainder

            if gain > best_gain:
                best_gain = gain
                best_info = [i, split_value]

    return best_info


def compute_info_content(data_list):
    result = get_numbers(data_list)
    list_len = len(data_list)
    res = 0.0
    for i, val in enumerate(result):
        log_value = 0.0
        probability = result[i] / list_len
        if probability != 0:
            log_value = probability * (math.log(probability) / math.log(2))
        res = res + log_value

    res = -res
    if res == -0:
        res = 0
    return res


def predict(root, data_list):
    for i in range(len(data_list)):
        node = root

        while node.label is None:
            if float(data_list[i][node.attr]) <= node.split_value:
                node = node.left_node
            else:
                node = node.right_node

        print(node.label)


if __name__ == '__main__':
    options, args = getopt.getopt(sys.argv, 'hn:w:', longopts=[])
    a = args[0]
    trainSetName = args[1]
    testSetName = args[2]
    min_leaf = int(args[3])

    f = open('./' + trainSetName)
    data = f.readlines()
    train_data_list = list()

    for i, val in enumerate(data):
        if i > 0:
            train_data_list.append(val.split())

    f = open('./' + testSetName)
    data = f.readlines()
    test_data_list = list()

    for i, val in enumerate(data):
        if i > 0:
            test_data_list.append(val.split())

    get_numbers(train_data_list)
    dtl = decision_tree_learning(train_data_list, min_leaf)
    predict(dtl, test_data_list)

    f.close()
