# CNOUS UUID (esc-uuid.jar)

## Algorithme de génération

L’algorithme de génération de l’UUID à utiliser est celui standardisé selon la norme RFC-4122 version 1.   
Cependant, Au lieu d’utiliser l’adresse physique MAC du serveur réalisant le calcul pour assurer l’unicité du numéro, celui-ci sera remplacé, comme le chapitre 4.5 de la RFC le permet, par un numéro sur 6 octet.  

Pour cela, le « Participant Identification Code  » (PIC) de l’établissement sera utilisé. Ce numéro européen sur 9 caractères est unique pour chaque établissement européen. L’université de Poitiers, a, par exemple le PIC : 999859608. 

Ce numéro, préfixé par un chiffre sur trois caractères forme une valeur sur 6 octet. Exemple pour l’université de Poitiers : 001999859608.  
 
Le préfixe doit être un entier positif au format décimal sur 3 caractères. Il permettra de distinguer les serveurs d’un même établissement si cet établissement veut générer des UUID sur plusieurs serveurs.

## Strucute de l'UUID
L'uuid est composé de 16 octects :
* Octet 0-3: time_low The low field of the timestamp
* Octet 4-5: time_mid The middle field of the timestamp
* Octet 6-7: time_hi_and_version The high field of the timestamp multiplexed with the version number
* Octet 8: clock_seq_hi_and_reserved The high field of the clock sequence multiplexed with the variant
* Octet 9: clock_seq_low The low field of the clock sequence
* Octet 10-15: node The spatially unique node identifier **=> dans note cas = Préfixe + PIC** 
