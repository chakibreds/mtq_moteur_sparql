# readlines of a file
lines = open('../data/STAR_ALL_workload.queryset').readlines()

nb_select = 0
line_number = 502
notworking = [502, 534, 543, 585, 601, 611, 613, 625, 628, 631, 645, 655, 657, 658, 670, 680, 690, 691, 693, 694, 696, 726, 800, 802, 808, 809, 819, 823, 825, 826, 828, 829, 830, 831, 833, 834, 836, 837, 839, 844, 852, 857, 861, 862, 864, 866, 867, 872, 875, 881, 887, 890, 899, 900, 929, 969, 978, 983, 984, 986, 996, 1002, 1003, 1008, 1013, 1015, 1016, 1018, 1021, 1023, 1024, 1025, 1027, 1028, 1029, 1038, 1040, 1043, 1044, 1047, 1049, 1052, 1053, 1056, 1058, 1060, 1061, 1062, 1063, 1065, 1068, 1069, 1076, 1077, 1078, 1079, 1080, 1082, 1083, 1084, 1086, 1087, 1089, 1091, 1092, 1093, 1094, 1096, 1099, 1127]
i=0

while i < len(lines):
    line=lines[i].strip()
    if line.startswith('SELECT'):
        nb_select += 1
    i+=1
    if nb_select in notworking:
        print(line)
        while (not lines[i].startswith('SELECT')):
            print(lines[i])
            i += 1


    