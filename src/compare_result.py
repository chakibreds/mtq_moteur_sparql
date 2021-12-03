import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

def convert_to_list(l):
    if l[0] == '[' and l[1] == ']': 
        return []
    if l[0] == '"' and l[-1] == '"':
        l = l[1:-1]
    if l[0] == '[' and l[-1] == ']': 
        l = l[1:-1]

    l = l.split(',') 
    for i in range(len(l)):
        l[i] = l[i].strip()
    return l


def equals_list(l1, l2):
    if len(l1) != len(l2):
        return False
    for i in l1:
        if i not in l2:
            return False
    return True

# read each coloumn from a csv file and put it in a list
def read_queries_results(file_name):
    df = pd.read_csv(file_name)
    
    return df

if __name__ == '__main__':
    jena_result = read_queries_results('../output/queryResult-jena.csv')
    qengine_result = read_queries_results('../output/queryResult-qengine.csv')
    
    print("Cheking differences...")

    assert len(jena_result) == len(qengine_result)

    ids = []
    for i in range(len(jena_result)):
        jena = convert_to_list(jena_result.iloc[i]['Result'])
        qengine = convert_to_list(qengine_result.iloc[i]['Result'])

        if not equals_list(jena,qengine):
            ids.append(jena_result.iloc[i]['Query'])

            if (len(jena) == len(qengine)):
                print("Query: " + str(jena_result.iloc[i]['Query']))
                print("Jena: " + str(jena))
                print("Qengine: " + str(qengine))
                print("\n")
            """
            print(jena_result.iloc[i]['Query'])
            print(jena_result.iloc[i]['Result'])
            print(qengine_result.iloc[i]['Result'])
            print('\n') 
            """
        

    if len(ids) == 0:
        print('All results are the same')
    else:
        print(f'{len(ids)} results are different out of {len(jena_result)}')
        print(ids,file=open("../output/different_queries.txt", "w"))
    